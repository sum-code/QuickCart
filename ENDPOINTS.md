# QuickCart API Endpoints

---

## AUTH MODULE
| HTTP Verb | Endpoint                    | Access  |
|-----------|-----------------------------|---------|
| POST      | /api/auth/register          | Public  |
| POST      | /api/auth/login             | Public  |
| POST      | /api/auth/logout            | Public  |
| GET       | /api/auth/debug-token       | Public  |
| GET       | /api/auth/debug-context     | Public  |

---

## USER MODULE
| HTTP Verb | Endpoint                    | Access        |
|-----------|-----------------------------|---------------|
| GET       | /api/user/me                | USER / ADMIN  |
| GET       | /api/user/ping              | USER / ADMIN  |

---

## PRODUCT MODULE
| HTTP Verb | Endpoint                              | Access        |
|-----------|---------------------------------------|---------------|
| GET       | /api/v1/products                      | Public        |
| GET       | /api/v1/products/{id}                 | Public        |
| POST      | /api/v1/admin/products                | ADMIN only    |
| PUT       | /api/v1/admin/products/{id}           | ADMIN only    |
| DELETE    | /api/v1/admin/products/{id}           | ADMIN only    |

---

## CART MODULE
| HTTP Verb | Endpoint                              | Access        |
|-----------|---------------------------------------|---------------|
| GET       | /api/user/cart                        | USER / ADMIN  |
| POST      | /api/user/cart/items                  | USER / ADMIN  |
| PUT       | /api/user/cart/items/{productId}      | USER / ADMIN  |
| DELETE    | /api/user/cart/items/{productId}      | USER / ADMIN  |
| DELETE    | /api/user/cart                        | USER / ADMIN  |

---

## ORDER MODULE — USER
| HTTP Verb | Endpoint                              | Access        |
|-----------|---------------------------------------|---------------|
| POST      | /api/user/orders                      | USER / ADMIN  |
| GET       | /api/user/orders                      | USER / ADMIN  |
| GET       | /api/user/orders/{orderId}            | USER / ADMIN  |
| GET       | /api/v1/orders                        | USER / ADMIN  |
| GET       | /api/v1/orders/{orderId}/tracking     | USER / ADMIN  |
| POST      | /api/v1/orders/{orderId}/return       | USER / ADMIN  |
| GET       | /api/v1/orders/{orderId}/invoice      | USER / ADMIN  |

## ORDER MODULE — ADMIN
| HTTP Verb | Endpoint                              | Access        |
|-----------|---------------------------------------|---------------|
| GET       | /api/admin/orders                     | ADMIN only    |
| PATCH     | /api/admin/orders/{orderId}/status    | ADMIN only    |
| GET       | /api/v1/admin/orders                  | ADMIN only    |
| PUT       | /api/v1/admin/orders/{orderId}/status | ADMIN only    |

---

## WISHLIST MODULE
| HTTP Verb | Endpoint                              | Access        |
|-----------|---------------------------------------|---------------|
| GET       | /api/v1/wishlist                      | USER / ADMIN  |
| POST      | /api/v1/wishlist/{productId}          | USER / ADMIN  |
| DELETE    | /api/v1/wishlist/{productId}          | USER / ADMIN  |

---

## REVIEW MODULE
| HTTP Verb | Endpoint                                       | Access        |
|-----------|------------------------------------------------|---------------|
| GET       | /api/v1/products/{productId}/reviews           | Public        |
| POST      | /api/v1/products/{productId}/reviews           | USER / ADMIN  |

---

## ADMIN MODULE
| HTTP Verb | Endpoint                              | Access        |
|-----------|---------------------------------------|---------------|
| GET       | /api/admin/ping                       | ADMIN only    |
| GET       | /api/admin/users                      | ADMIN only    |
| POST      | /api/v1/admin/dev/cleanup-test-users  | ADMIN only    |
