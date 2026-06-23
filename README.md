# DemoWebShop E2E Automation

End-to-end UI automation for the **registration → digital-downloads → verify-in-cart** flow on
[demowebshop.tricentis.com](https://demowebshop.tricentis.com), built with **Java 17 +
Selenium 4 + TestNG** using a **Page Object + Component** model.

## Run it

**Prerequisites:** JDK 17+, Maven 3.9+, and Chrome installed locally. (Firefox also works.)
The browser driver is resolved automatically by Selenium Manager — nothing to download.

```bash
git clone <repo-url>
cd demowebshop-automation
mvn clean test
```

That runs the full suite (one happy-path test + one negative test) against the live site.

**Useful overrides** (all optional; defaults: local Chrome, visible, 15s waits, 1 retry):

```bash
mvn clean test -Dheadless=true                 # run headless (used by CI)
mvn clean test -Dbrowser=firefox               # switch browser
mvn clean test -Dtimeout=20                    # change the explicit-wait budget (seconds)
mvn clean test -Dretry.count=0                 # disable the flaky-test retry
```

CI runs the suite headless on every push/PR — see [`.github/workflows/e2e.yml`](.github/workflows/e2e.yml)
(GitHub Actions; runners have Chrome + open egress) and `.gitlab-ci.yml` (GitLab equivalent).

> **Running in a locked-down sandbox?** Some CI/agent environments block outbound traffic by
> allowlist. If the run fails with `403 host_not_allowed` for `demowebshop.tricentis.com`, add that
> host to the environment's network egress settings; the suite needs to reach the live site.

## What's covered

The happy-path test (`RegistrationAndDigitalDownloadsTest`) follows the brief's 11 steps 1:1:

1. Open the shop
2. Open **Register** from the header
3–4. Fill personal details + password (gender, first/last name, unique e-mail, password + confirm)
5. Submit registration
6. Click **Continue**
7. Assert the registered e-mail appears in the header (proves the user is logged in)
8. Open **Digital Downloads**
9. Add a **random** product to the cart and confirm the add was acknowledged
10. Open the **Shopping cart**
11. **Assert the cart contains the exact product selected in step 9** — the final acceptance
    criterion. Asserting the product *name*, not just the header counter, is what proves the
    *right* item reached the cart (a counter-only check would pass even on the wrong product).

Beyond the brief, `RegistrationNegativeTest` verifies that re-using an existing e-mail is
rejected ("The specified email already exists") — because a tester checks the unhappy path,
not just the happy one. A wider exploratory defect charter lives in [`DEFECTS.md`](DEFECTS.md).

## API & backend tests

Alongside the UI suite there's a fast, **offline API/backend layer** (Java + REST Assured +
TestNG) that runs against a **WireMock mock backend** serving JSON fixtures — no live site, no
browser, deterministic. It's the broad base of the test pyramid beneath the slow UI E2E tests.

```bash
mvn test -Papi          # run the API/backend suite (no network needed)
```

- **Standard:** [`API_TEST_STANDARD.md`](API_TEST_STANDARD.md) — a 7-step recipe for writing one.
- **Worked example:** [`RegistrationApiTest`](src/test/java/com/demoshop/api/tests/RegistrationApiTest.java)
  — registration happy path + duplicate-e-mail rejection, asserting status, JSON schema, and body.
- **Mock data:** [`src/test/resources/mockdata/`](src/test/resources/mockdata/); skeletons for the
  catalogue, cart, and a backend contract test are stubbed with the recipe and ready to fill in.

## Design

```
RegistrationAndDigitalDownloadsTest          ← reads like the feature it verifies
        │  uses
        ▼
HeaderComponent  RegisterPage  RegistrationResultPage  DigitalDownloadsPage  ShoppingCartPage
        └────────────────────── all extend ──────────────────────┘
                              │
                          BasePage              ← one explicit-wait interaction layer
                              │
                        DriverFactory            ← ThreadLocal WebDriver (parallel-safe)
                          + Config                ← overridable via -D system properties
                        User (factory)            ← unique test data per run

listeners/  ScreenshotListener (shot on failure)   RetryAnalyzer + RetryListener (flake guard)
```

- **Page Object + Component.** Each page is its own object; the header is a *component*
  because it's shared across pages, not owned by any single one. Actions return the next
  page object, so the test reads as a flow.
- **Locators: stable IDs first.** The site is nopCommerce, so it exposes real element IDs
  (`#FirstName`, `#register-button`, …); cart/grid rows use stable nopCommerce classes
  (`.cart-item-row`, `.product-name`). Zero XPath, no PageFactory.
- **Waits: explicit only.** One condition-based wait strategy lives in `BasePage`. There is
  no `Thread.sleep` anywhere. The async cart-counter update is handled with a `textToBe`
  wait, not a fixed pause.
- **Test data: generated, never hardcoded.** `User.randomUser()` mints a unique
  `@example.com` address every run, so registration never collides on a duplicate e-mail.
- **Assertions: specific messages.** Every assertion names what failed and, for the cart
  steps, which random product was exercised (and what the cart actually held on a mismatch).

## Observability & robustness

A test you can't debug from its output isn't done. This suite ships with:

- **Real logging — SLF4J + Logback.** Each run logs the customer e-mail, the randomly chosen
  product, and per-test START/END to the console *and* `target/logs/test-run.log`. (`slf4j-api`
  is pinned to 2.0.x so it binds to Logback instead of silently falling back to a no-op logger —
  a trap when TestNG drags in an older 1.7.x transitively.)
- **Screenshot on failure — `ScreenshotListener`.** On any test *or setup* failure it saves a
  PNG to `target/screenshots/` and logs the path. CI uploads these as artifacts.
- **Flaky-test retry — `RetryAnalyzer` + `RetryListener`.** A single bounded retry (configurable
  via `-Dretry.count`) absorbs transient live-site hiccups without masking a repeatable failure.
  Applied to every `@Test` automatically via an `IAnnotationTransformer` — no per-test annotation.
- **Parallel-safe by class.** `testng.xml` runs test classes in parallel (`parallel="classes"`);
  the ThreadLocal driver makes this safe. (Method-level parallelism is deliberately avoided —
  the driver/header are per-instance fields.)

## Trade-offs & assumptions

- **Tests run against the live public site, not a mock.** That's what the brief targets and
  it exercises the real flow, but it couples the suite to the site's uptime and data. For a
  product team I'd point this at a controlled environment with seeded data.
- **`#gender-male` is selected** to satisfy the form; gender is not part of what's being
  validated, so the choice is arbitrary and documented here rather than parameterised.
- **The random product is chosen from whatever is on the Digital Downloads grid at runtime.**
  This adds coverage breadth (any product must be addable) at the cost of a fixed target. The
  chosen name is logged and carried into the step-11 cart assertion, so any failure is traceable.
- **One narrow `catch (TimeoutException)`** exists in `HeaderComponent.cartQuantityIs` — only
  to convert a timeout into a clean, descriptive assertion failure. Nothing else is caught.

## With more time

- Point `DriverFactory` at a remote Grid / Selenoid / Perfecto via `RemoteWebDriver` and run
  cross-browser in parallel (the ThreadLocal driver already makes this safe).
- Use the new API layer for **setup**: create the user via REST so the UI test starts
  logged-in, cutting registration out of every cart test. (The API/backend suite itself is
  already in place — see [`API_TEST_STANDARD.md`](API_TEST_STANDARD.md).)
- Wire **Allure** for richer reporting (the failure screenshots would attach to each step).
- Cover more unhappy paths from `DEFECTS.md` (password mismatch, invalid e-mail format, empty
  required fields, cart quantity math).
- Request `data-testid` attributes from the dev team for any element that lacks a stable ID,
  to harden locators against markup changes.

## Tooling note

Project scaffolding was drafted with AI assistance; all locators follow nopCommerce's stable
IDs/classes and the design choices above are deliberate.
