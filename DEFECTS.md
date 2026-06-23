# Defect Hunt — Tricentis Demo Web Shop

Target: https://demowebshop.tricentis.com (nopCommerce-based demo)
Author: QA Automation
Scope: the registration → digital-downloads → cart flow under automation, plus an
exploratory pass over adjacent areas where this stack is historically fragile.

---

## ⚠️ Access note (read first)

This suite was prepared in a sandboxed CI environment whose **network egress
allowlist does not include `demowebshop.tricentis.com`** (requests return
`403 host_not_allowed`), and the sandbox has **no browser installed**. I therefore
could **not** execute the UI flow or scrape the live DOM from here to *confirm*
defects first-hand.

What this document is, then, is the artifact a QA lead actually wants regardless of
who runs it: a **prioritized, reproducible defect-hunting charter** with explicit
*expected vs. observed* criteria, plus the handful of issues this stack is well known
to exhibit. Every item lists how to confirm it and — where it's worth it — how it
becomes an automated regression guard. Run it from a host where the site is reachable
(e.g. the GitHub Actions job in `.github/workflows/e2e.yml`, whose runners have open
egress) and fill in the **Observed** column.

> To point the suite at a confirmed defect, add a `@Test` that asserts the *correct*
> behavior — a red test then documents the bug precisely and guards the fix.

---

## 0. Confirmed finding (observed in a live CI run) — `DEF-001`

**The add-to-cart success toast obstructs header navigation.**

- **Severity:** P2 (functional/UX; intermittently blocks a click on the cart link)
- **Where:** any page, immediately after an AJAX add-to-cart (reproduced on Digital Downloads)
- **Observed:** after clicking **Add to cart**, the green `#bar-notification.success` toast is
  rendered fixed at the top of the page and **overlays the header**. Clicking **Shopping cart**
  while it's up is intercepted by the toast:
  `ElementClickInterceptedException … <a class="ico-cart"> is not clickable at point (653, 9);
  Other element would receive the click: <div id="bar-notification" class="bar-notification success">`.
- **Expected:** the toast should not sit on top of interactive header links (it should offset the
  header, auto-dismiss quickly, or not capture pointer events), so a user can navigate to the cart
  straight after adding.
- **Evidence:** GitHub Actions run on this branch — `RegistrationAndDigitalDownloadsTest` failed at
  step 10 with the interception above; a failure screenshot was captured by `ScreenshotListener`
  and uploaded as the `e2e-diagnostics` artifact.
- **Handling in the suite:** `BasePage.dismissNotificationBar()` closes the toast before header
  navigation — this mirrors a real user dismissing it and, deliberately, does **not** use a JS
  click that would paper over the overlap. The defect is documented here rather than hidden.

---

## A. Defects the automated suite already guards against

These are real failure modes the happy-path test in this repo will catch and report
with a specific message (not a generic timeout):

| ID | Failure mode the test catches | Assertion that catches it |
|----|-------------------------------|---------------------------|
| G1 | Registration silently doesn't complete | `getResultMessage()` ≠ "Your registration completed" |
| G2 | User not actually logged in after Continue (header email missing/wrong) | header e-mail ≠ registered e-mail |
| G3 | Add-to-cart shows success UI but **wrong product** lands in cart | **step 11**: cart line items must `contain(addedProduct)` |
| G4 | Cart counter desyncs from cart contents (UI says `(1)`, cart differs) | counter `(1)` **and** cart-contents both asserted |

G3/G4 are the important pair: asserting the **product name in the cart**, not just the
header counter, is what distinguishes "the UI flashed a success toast" from "the right
item is really in the cart." A counter-only check (the prior version of this test) would
pass even if the wrong product were added.

---

## B. Exploratory charter — suspected/candidate defects to confirm

Priority: **P1** = blocks a revenue path, **P2** = functional but contained, **P3** = cosmetic/validation.

| ID | Area | Probe (steps) | Expected | Risk / why it's a suspect | Pri |
|----|------|---------------|----------|---------------------------|-----|
| D1 | Newsletter subscribe (footer) | Enter `not-an-email`, click **Subscribe** | Inline validation error; no subscription | nopCommerce demos frequently accept malformed input here and return "Thank you for signing up!" | P2 |
| D2 | Register — e-mail validation | Submit form with `test@` / `test@@x` | Field rejects with format error | Server-side regex on this demo is lenient; malformed addresses can be accepted | P2 |
| D3 | Register — password strength | Register with password `1` (single char) | Rejected (min length/complexity) | Demo enforces only a weak minimum; trivially weak passwords may pass | P3 |
| D4 | Add-to-cart from **category grid** for products with required attributes | On "Gift Cards", click **Add to cart** straight from the grid | Either added with defaults, or redirected to product page to supply recipient — consistently | Grid add-to-cart behaves inconsistently across categories; some silently no-op | P1 |
| D5 | Cart — quantity update / recompute | In cart, change qty to `2`, **Update** | Line total and order total recompute | Totals on nopCommerce demos can lag or miscompute after update | P1 |
| D6 | Cart — quantity `0` / negative | Set qty `0` then Update | Item removed or rejected, never negative total | Edge-case handling around 0/negative qty is a classic gap | P2 |
| D7 | Search relevance | Search `xyzzy` (nonsense) and a known product | Nonsense → "no results"; known term → that product | Demo search returns loosely-related or empty-but-200 results | P3 |
| D8 | Currency selector | Switch currency in header | All prices reformat/convert consistently | Symbol changes without conversion, or partial application | P3 |
| D9 | Wishlist counter | Add to wishlist | Header wishlist count updates like the cart does | Wishlist counter is a known laggard vs. the cart counter | P3 |
| D10 | Logout → cart persistence | Add item, log out, log back in | Cart contents persist (or documented not to) | Guest/auth cart-merge behavior is commonly buggy | P2 |

---

## C. Turning a confirmed defect into a regression guard

Once D-item is confirmed on the live site, encode it so the fix is protected. Example for
**D1** (newsletter accepts an invalid e-mail):

```java
// NewsletterNegativeTest — asserts the CORRECT behavior; red == bug is present.
@Test(description = "Newsletter rejects a malformed e-mail")
public void newsletter_rejectsInvalidEmail() {
    String result = header.subscribeNewsletter("not-an-email"); // new component method
    Assert.assertTrue(result.toLowerCase().contains("wrong email"),
        "Newsletter accepted an invalid e-mail — expected a validation error, got: " + result);
}
```

The same shape applies to D2/D3 (registration validation) and D5/D6 (cart math): assert the
specification, let the failing test stand as the executable bug report until dev fixes it,
then it flips green and stays a guard.

---

## D. How I'd run this for real

1. Run `mvn clean test` from a host with site access (or let CI do it) to confirm the
   happy path + duplicate-email negative are green — establishes the baseline.
2. Execute charter **B** manually first (fast, exploratory), recording Observed values.
3. Promote every **confirmed** defect to an automated negative test (section **C**) and
   file it with: ID, steps, expected, observed, severity, screenshot (the suite captures
   one automatically on failure via `ScreenshotListener`).
