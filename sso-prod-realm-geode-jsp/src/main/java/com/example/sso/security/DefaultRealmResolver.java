
package com.example.sso.security;

import com.example.sso.util.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultRealmResolver implements RealmResolver {

  @Override
  public String resolveRealm(HttpServletRequest request) {

    // 1) explicit realm param
    String realm = request.getParameter("realm");
    if (StringUtils.hasText(realm)) return realm.trim().toUpperCase();

    // 2) clientID param (entry)
    realm = request.getParameter("clientID");
    if (StringUtils.hasText(realm)) return realm.trim().toUpperCase();

    // 3) session stored realm (avoid "flip" between tabs)
    HttpSession session = request.getSession(false);
    if (session != null) {
      Object r = session.getAttribute(SessionKeys.REALM);
      if (r instanceof String s && StringUtils.hasText(s)) return s;
    }

    // 4) header
    realm = request.getHeader("X-REALM");
    if (StringUtils.hasText(realm)) return realm.trim().toUpperCase();

    return "DEFAULT";
  }
}
