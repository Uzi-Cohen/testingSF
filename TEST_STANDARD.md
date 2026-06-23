# Test Standard — New Customer Registration & Digital Download Purchase

**Test ID:** TC-REG-DD-001
**Type:** End-to-End, UI, Functional (positive path)
**Application under test:** Tricentis Demo Web Shop — https://demowebshop.tricentis.com
**Priority:** High (registration and add-to-cart are critical revenue paths)

---

## 1. Test Purpose

Validate that a brand-new customer can complete the core onboarding-to-cart journey on the
Demo Web Shop: register an account, reach an authenticated session, navigate the catalog, and
add a product to the shopping cart. The test confirms that the registration form, the
post-registration logged-in state, category navigation, and the add-to-cart mechanism all work
together as one continuous user flow — not just as isolated features.

Success means a new user goes from the home page to "product in cart" without error, and the
application reflects each state change (registration confirmed, user logged in, cart updated).

## 2. Preconditions

- The Demo Web Shop is reachable and responsive at the base URL.
- A supported browser is available (Chrome by default; Firefox supported).
- **No pre-existing account is required.** The test generates a unique e-mail address at
  runtime, so it never depends on — or conflicts with — existing data.
- The Digital Downloads category contains at least one purchasable product.
- Test data used:
  - First name: `Test`, Last name: `Automation`
  - E-mail: unique per run (`test.automation.<timestamp><salt>@example.com`)
  - Password: `Test@12345` (throwaway test value, meets the site's minimum length)

## 3. Steps to Execute

| # | Action | Expected Result |
|---|--------|-----------------|
| 1 | Navigate to the base URL | Home page loads; header shows **Register** and **Log in** links; cart shows `(0)` |
| 2 | Click **Register** in the header | The registration form (`/register`) is displayed |
| 3 | Fill personal details: select gender, enter first name, last name, and the unique e-mail | Fields accept the input and show the entered values |
| 4 | Enter the password and the matching confirmation password | Both password fields accept the input |
| 5 | Click **Register** | Registration succeeds; a confirmation page shows **"Your registration completed"** |
| 6 | Click **Continue** | User is returned to the home page in an authenticated session |
| 7 | Inspect the header | The registered e-mail address is displayed in the header (confirms the user is logged in) |
| 8 | Click **Digital Downloads** in the top navigation | The Digital Downloads category page loads with its product grid |
| 9 | Select a random product on the page and click **Add to cart** | A green success bar appears — *"The product has been added to your shopping cart"* — and the header cart counter updates from `(0)` to `(1)` |
| 10 | Click **Shopping cart** in the header | The shopping cart page (`/cart`) loads with its line-item table |
| 11 | Inspect the cart's line items | A line item with **exactly the product name selected in step 9** is present in the cart |

## 4. Post-Conditions

- A new customer account now exists on the site and is left in a logged-in state with one item
  in its cart. Because the e-mail is unique per run, this residual data is harmless and does not
  affect re-runs.
- **Cleanup:** the browser session is closed in teardown after every test (the driver is quit
  and removed), so each run starts from a clean, isolated browser with no carried-over cookies,
  cache, or session.
- No server-side cleanup is performed — the Demo Web Shop is a throwaway sandbox and exposes no
  account-deletion API. In a real environment, post-conditions would include deleting the
  created account (ideally via API) to keep the data store clean.

## 5. Validation Criteria

The test is considered **passed** only if every one of the following holds:

1. **Registration confirmed** — the confirmation page displays exactly "Your registration
   completed" after submitting the form.
2. **Authenticated state reached** — after **Continue**, the header displays the *same* e-mail
   address that was used to register. This is the explicit proof of a logged-in session
   required by the brief.
3. **Add-to-cart succeeded** — the success notification bar is shown and its text confirms the
   product reached the shopping cart.
4. **Cart state changed** — the header cart counter reflects exactly one item, `(1)`, verifying
   the add was actually persisted rather than only visually acknowledged.
5. **Correct product in cart** — on the shopping cart page, a line item whose name **exactly
   matches the product selected in step 9** is present. This is the brief's final acceptance
   criterion: it proves the *right* item reached the cart, not merely that *some* item did (a
   counter-only check would pass even if the wrong product were added).

Any failed assertion fails the test. Assertion messages identify what failed and — for the
add-to-cart and cart-verification steps — which randomly selected product was being exercised
(and, on mismatch, what the cart actually contained), so a failure is diagnosable from the log
alone. A screenshot is captured automatically on any failure.

**Out of scope for this case:** payment/checkout, cart quantity edits, removing items, e-mail
verification, and form-field validation (these are covered separately; one negative
registration case — duplicate e-mail rejection — is included alongside this happy path in the
automation suite). A broader exploratory defect charter for the site lives in `DEFECTS.md`.
