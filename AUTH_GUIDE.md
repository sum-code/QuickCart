# QuickCart Authentication Guide

This document explains the current authentication setup in QuickCart:
- Email/Password Signup
- Email/Password Login
- Google OAuth2 Login/Signup
- Admin vs User role behavior
- Testing steps (Postman + Browser)

---

## 1) Current Auth Model (Summary)

QuickCart supports 2 auth methods:

1. **Email + Password**
2. **Google OAuth2**

Role model:
- `USER`
- `ADMIN`

Important behavior:
- New Google users are created as `USER` by default.
- Admin access is controlled by backend role checks.
- Protected APIs require JWT Bearer token.

---

## 2) Main Backend Endpoints

### Public endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /oauth2/authorization/google` (starts OAuth2 flow)
- `GET /login/oauth2/code/google` (Google callback to backend)

### Protected endpoints
- `GET /api/user/me` (requires USER or ADMIN)
- `/api/admin/**` (requires ADMIN)

---

## 3) Signup (Email/Password)

## Endpoint
`POST /api/auth/register`

## Request body (USER)
```json
{
  "email": "user1@gmail.com",
  "password": "123456",
  "role": "USER"
}
```

## Request body (ADMIN)
```json
{
  "email": "admin1@gmail.com",
  "password": "123456",
  "role": "ADMIN",
  "adminCode": "QuickCart"
}
```

Notes:
- `role` is optional in some flows; default behavior is USER when role is not provided.
- For admin signup, `adminCode` must match backend config.

---

## 4) Login (Email/Password)

## Endpoint
`POST /api/auth/login`

## Request body
```json
{
  "email": "user1@gmail.com",
  "password": "123456"
}
```

## Response
Backend returns auth data including JWT token and user info.
Use this token for protected APIs.

---

## 5) Google OAuth2 Flow (Step by Step)

### Step 1: User clicks Google button (frontend)
Frontend opens:
`GET http://localhost:8080/oauth2/authorization/google`

### Step 2: Backend redirects to Google
Spring Security sends HTTP `302` to Google login/consent page.

### Step 3: User authenticates on Google
Google verifies user identity.

### Step 4: Google redirects to backend callback
Google calls:
`GET /login/oauth2/code/google`

### Step 5: Backend success handler runs
Backend logic:
- Reads Google email
- If email exists in DB: uses existing user
- If email does not exist: creates new user with `USER` role
- Generates JWT token
- Redirects to frontend callback with token (query params)

### Step 6: Frontend callback page processes token
Frontend route:
`/auth/oauth2/callback`

Frontend callback page:
- Reads token (and optional email/roles) from URL
- Saves auth session in local storage (Zustand store)
- Optionally calls `/api/user/me`
- Redirects user based on role:
  - ADMIN -> `/admin/dashboard`
  - USER -> `/`

---

## 6) Where Token and User Data Are Stored

Current frontend behavior:
- Auth state is stored in **localStorage** (Zustand persist)
- Stored values include:
  - `accessToken`
  - `user` (with roles)

Current backend behavior:
- JWT is stateless and not persisted server-side.
- User and role mappings are persisted in database tables (`users`, `roles`, `app_user_roles`).

---

## 7) Postman Testing Guide

Use base URL:
`http://127.0.0.1:8080`

### A) Register USER
`POST /api/auth/register`
```json
{
  "email": "user1@gmail.com",
  "password": "123456",
  "role": "USER"
}
```

### B) Register ADMIN
`POST /api/auth/register`
```json
{
  "email": "admin1@gmail.com",
  "password": "123456",
  "role": "ADMIN",
  "adminCode": "QuickCart"
}
```

### C) Login
`POST /api/auth/login`
```json
{
  "email": "user1@gmail.com",
  "password": "123456"
}
```

Copy `token` from response.

### D) Test protected endpoint
`GET /api/user/me`
Header:
`Authorization: Bearer <token>`

---

## 8) Can Google OAuth be fully tested in Postman?

Short answer: **not fully** for your current setup.

Reason:
- Google OAuth uses browser redirects and consent UI.
- Your backend flow redirects to frontend callback URL.

Recommended method:
1. Start backend + frontend
2. Use browser for Google login (`/oauth2/authorization/google`)
3. Use Postman to test token-based APIs after token is obtained

---

## 9) Role Protection Rules (Current)

Backend security rules:
- `/api/auth/**`, `/oauth2/**`, `/login/**` -> permit all
- `/api/admin/**` -> ADMIN only
- `/api/user/**` -> USER or ADMIN
- all others -> authenticated

Frontend route guards:
- `ProtectedRoute` -> requires token + user
- `AdminRoute` -> requires token + user + ADMIN role

---

## 10) Quick Troubleshooting

### "ERR_CONNECTION_REFUSED"
Backend not running on expected port (8080).

### "Could not send request" in Postman
Usually Postman connectivity/agent/proxy issue, or wrong URL.
Use `127.0.0.1` and ensure backend is running.

### Google OAuth starts but fails callback
Check:
- Google Console redirect URI exactly matches backend callback
- Backend OAuth properties are correct
- Frontend callback route exists and is reachable

---

## 11) Suggested Security Improvement (Important)

Do not keep real secrets in committed `application.properties` for production:
- DB password
- Google client secret
- JWT secret

Prefer environment variables and secret managers.
