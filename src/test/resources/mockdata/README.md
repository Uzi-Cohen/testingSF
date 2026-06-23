# Mock data

Static JSON the mock backend ([`MockBackend`](../../java/com/demoshop/api/mock/MockBackend.java))
serves for the API/backend suite. Keeping the data here — separate from the test code — means a
scenario's expected payload is readable on its own and editable without touching Java.

| File | Endpoint served | Represents |
|------|-----------------|------------|
| `registration-success.json` | `POST /api/customer/register` (new e-mail) → 201 | Successful registration |
| `registration-duplicate-email.json` | `POST /api/customer/register` (known e-mail) → 409 | Rejected duplicate e-mail |
| `digital-downloads-catalog.json` | `GET /api/catalog/digital-downloads` → 200 | The product catalogue |
| `cart-with-item.json` | `POST /api/cart/items` → 201, `GET /api/cart` → 200 | A cart holding one item |

> `{{ ... }}` in `registration-success.json` is a [WireMock response template](https://wiremock.org/docs/response-templating/):
> `{{jsonPath request.body '$.email'}}` echoes back the e-mail the caller sent, so a test can prove
> its input reached the backend rather than matching a hard-coded value.
