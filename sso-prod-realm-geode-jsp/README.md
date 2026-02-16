
# sso-prod-realm-geode-jsp

Production-ready reference project (runs out-of-the-box with in-memory Spring Session; switch to Geode in prod).

## What this includes
- Realm-wise session cookies: `SSOSESSION_<REALM>` (SameSite=None; Secure; HttpOnly)
- Realm-wise CSRF cookies: `XSRF-TOKEN_<REALM>` + JSP hidden `_csrf` via common fragment
- Device binding per realm: `DEVICE_<REALM>` cookie binds the session to the browser (prevents reuse across browsers/incognito)
- Multi-tab safe OAuth request capture via `state` -> session map
- Interceptors:
  - `/ssoauthenticate` capture query params into session
  - global session validation (redirect to `/login` if not authenticated)
  - device binding check
- YAML-driven per-client policy (features + session timeouts + concurrent login)
- Factory pattern for Auth (internal vs RC external API stub)
- OAuth provider strategy pattern (BC/RC/IHS stubs)
- Cleanup after OAuth success: removes stored request from session map

## Run
```bash
./gradlew bootRun
```

Open:
```
http://localhost:8080/ssopartner/ssoauthenticate?clientID=BC&response_type=code&redirecturl=https://example.com/cb&state=abc&scope=openid
```

Login:
- password: `pass`
- username: `expire` (triggers password-change for realms with passwordExpiryRedirect)
- username: `locked` (simulates locked)

## Switch to Geode (GemFire) sessions
1. Uncomment dependency in build.gradle:
   `org.springframework.session:spring-session-data-geode`
2. Remove `SpringSessionDevConfig` (in-memory)
3. Add your Geode ClientCache + Region config and properties.

## PCF / HTTPS
- Uses `SameSite=None` + `Secure` cookies
- `server.forward-headers-strategy=native` for reverse proxy headers
