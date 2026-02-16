
package com.example.sso.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.UUID;

public class RealmWiseCookieCsrfTokenRepository implements CsrfTokenRepository {

  private final RealmResolver realmResolver;

  private final String headerName = "X-XSRF-TOKEN";
  private final String parameterName = "_csrf";
  private final String cookiePrefix = "XSRF-TOKEN_";

  private final boolean secure = true;
  private final String sameSite = "None";
  private final boolean httpOnly = false;

  public RealmWiseCookieCsrfTokenRepository(RealmResolver realmResolver) {
    this.realmResolver = realmResolver;
  }

  @Override
  public CsrfToken generateToken(HttpServletRequest request) {
    return new DefaultCsrfToken(headerName, parameterName, newTokenValue());
  }

  @Override
  public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
    String realm = normalize(realmResolver.resolveRealm(request));
    String cookieName = cookiePrefix + realm;

    String path = request.getContextPath();
    if (!StringUtils.hasText(path)) path = "/";

    if (token == null) {
      response.addHeader("Set-Cookie", buildSetCookie(cookieName, "", path, secure, sameSite, 0, httpOnly));
      return;
    }
    response.addHeader("Set-Cookie", buildSetCookie(cookieName, token.getToken(), path, secure, sameSite, -1, httpOnly));
  }

  @Override
  public CsrfToken loadToken(HttpServletRequest request) {
    String realm = normalize(realmResolver.resolveRealm(request));
    String cookieName = cookiePrefix + realm;

    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;

    for (Cookie c : cookies) {
      if (cookieName.equals(c.getName()) && StringUtils.hasText(c.getValue())) {
        return new DefaultCsrfToken(headerName, parameterName, c.getValue());
      }
    }
    return null;
  }

  private String newTokenValue() {
    String raw = UUID.randomUUID() + ":" + UUID.randomUUID();
    return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
  }

  private String buildSetCookie(String name, String value, String path, boolean secure, String sameSite,
                                int maxAgeSeconds, boolean httpOnly) {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append("=").append(value == null ? "" : value);
    sb.append("; Path=").append(path);
    if (maxAgeSeconds >= 0) sb.append("; Max-Age=").append(maxAgeSeconds);
    if (secure) sb.append("; Secure");
    if (httpOnly) sb.append("; HttpOnly");
    if (StringUtils.hasText(sameSite)) sb.append("; SameSite=").append(sameSite);
    return sb.toString();
  }

  private String normalize(String realm) {
    if (!StringUtils.hasText(realm)) return "DEFAULT";
    return realm.trim().toUpperCase();
  }
}
