# API & Backend Test Standard

How we write API/backend tests in this repo. It's deliberately short: **one 7-step recipe**,
a worked example, and the few rules that keep the suite trustworthy. If a test follows the
recipe, it's good enough to merge.

**Canonical example:** [`RegistrationApiTest`](src/test/java/com/demoshop/api/tests/RegistrationApiTest.java)
— copy its shape. **Run:** `mvn test -Papi`.

---

## Where these tests sit

The [test pyramid](https://martinfowler.com/articles/practical-test-pyramid.html): API/backend
tests are the **fast, broad base** — they hit the backend contract directly, run in
milliseconds, and need no browser. The Selenium UI suite is the **narrow top**: few, slow, high
confidence. Prefer pushing a check *down* the pyramid: if it can be proven at the API level, it
belongs here, not in a UI E2E test.

Tests run against a **mock backend** ([WireMock](https://wiremock.org/)) that serves the JSON in
[`src/test/resources/mockdata/`](src/test/resources/mockdata/). That makes them deterministic and
offline — no live site, no flake.

## The 7-step recipe

Every test is exactly these steps, in this order — this is the
[Arrange-Act-Assert](https://semaphore.io/blog/aaa-pattern-test-automation) /
Given-When-Then shape REST Assured is built around:

| # | Step | What it means |
|---|------|---------------|
| 1 | **Name it for the behaviour** | `endpoint_condition_expectedResult` (e.g. `register_withNewEmail_returns201AndConfirmsRegistration`). One behaviour per test. |
| 2 | **Arrange** | Build isolated test data (a POJO/record via a factory — never a hand-written JSON string). The mock backend is already stubbed by `BaseApiTest`. |
| 3 | **Act** | Make **exactly one** API call — the operation under test. |
| 4 | **Assert the status code** | Check it first (`.statusCode(201)`). A 4xx/5xx makes every body assertion meaningless. |
| 5 | **Assert the body** | Validate the response **schema** and the **key fields** — not just the status. A 200 with the wrong payload is still a bug. |
| 6 | **Make failures diagnosable** | Every assertion carries an *expected vs. actual* message. A failure must be readable from the log alone. |
| 7 | **Keep it independent & fast** | No dependence on other tests or ordering; no real network. The mock resets before each test, so tests run in any order, in parallel. |

> These map to **FIRST**: **F**ast (7), **I**ndependent (7), **R**epeatable (2, 7),
> **S**elf-validating (4–6), **T**imely (write the test with the endpoint).

## What every API test asserts (step 5, expanded)

1. **Status code** — the right code for the outcome (`201` create, `200` read, `409` conflict…).
2. **Schema** — the response matches its published JSON Schema in
   [`src/test/resources/schema/`](src/test/resources/schema/). This is the *contract*: it catches
   a renamed/removed/retyped field even when values look fine.
3. **Key fields** — the values that prove the behaviour (e.g. the confirmation message, and that
   the response echoes the e-mail that was sent).

## Test data & the mock backend

- **Generate, don't hard-code.** Build request bodies from factories
  (`RegistrationRequest.from(User.randomUser())`) — the same `User` factory the UI suite uses.
- **Mock data lives as JSON**, not in Java — see
  [`mockdata/README.md`](src/test/resources/mockdata/README.md). To add a scenario: drop a JSON
  fixture, stub it in [`MockBackend`](src/test/java/com/demoshop/api/mock/MockBackend.java),
  reference the endpoint constant in [`ApiConfig`](src/test/java/com/demoshop/api/client/ApiConfig.java).
- **One source of truth for paths.** Endpoints are constants in `ApiConfig`, used by both the
  stubs and the tests — a path is never spelled out twice.

## Conventions

- **Layered, like the UI suite:** `client/` (config) · `model/` (records) · `mock/` (WireMock) ·
  `base/` (lifecycle) · `tests/`. Test logic never builds raw HTTP or JSON by hand.
- **Cover the unhappy path too.** Every endpoint gets at least one negative test (bad input,
  duplicate, not-found) — see the 409 case in the sample.
- **Skeletons** (`DigitalDownloadsApiTest`, `CartApiTest`, `CatalogContractTest`) are stubbed with
  this recipe as a checklist and `SkipException` until implemented — so the suite stays green and
  the next test is a fill-in-the-blanks job.
