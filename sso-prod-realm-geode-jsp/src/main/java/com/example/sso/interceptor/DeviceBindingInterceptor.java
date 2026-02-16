
package com.example.sso.interceptor;

import com.example.sso.security.RealmResolver;
import com.example.sso.util.RedirectUtil;
import com.example.sso.util.SessionKeys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class DeviceBindingInterceptor implements HandlerInterceptor {

  private final RealmResolver realmResolver;

  public DeviceBindingInterceptor(RealmResolver realmResolver) {
    this.realmResolver = realmResolver;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    HttpSession session = request.getSession(false);
    if (session == null) return true;

    String realm = normalize(realmResolver.resolveRealm(request));
    String deviceCookieName = "DEVICE_" + realm;

    String cookieValue = readCookie(request, deviceCookieName);
    String sessionValue = (String) session.getAttribute(SessionKeys.DEVICE_KEY);

    // First time bind
    if (!StringUtils.hasText(sessionValue)) {
      if (!StringUtils.hasText(cookieValue)) {
        String newDeviceKey = UUID.randomUUID().toString();
        session.setAttribute(SessionKeys.DEVICE_KEY, newDeviceKey);
        writeDeviceCookie(request, response, deviceCookieName, newDeviceKey);
        return true;
      }
      session.setAttribute(SessionKeys.DEVICE_KEY, cookieValue);
      return true;
    }

    // Validate
    if (!StringUtils.hasText(cookieValue) || !sessionValue.equals(cookieValue)) {
      session.invalidate();
      response.sendRedirect(RedirectUtil.url(request, "/login"));
      return false;
    }

    return true;
  }

  private String readCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;
    for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
    return null;
  }

  private void writeDeviceCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
    String path = request.getContextPath();
    if (!StringUtils.hasText(path)) path = "/";

    response.addHeader("Set-Cookie",
      name + "=" + value +
      "; Path=" + path +
      "; HttpOnly; Secure; SameSite=None"
    );
  }

  private String normalize(String realm) {
    if (!StringUtils.hasText(realm)) return "DEFAULT";
    return realm.trim().toUpperCase();
  }
}
