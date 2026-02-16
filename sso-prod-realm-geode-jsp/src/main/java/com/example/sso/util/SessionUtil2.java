
package com.example.sso.util;

import jakarta.servlet.http.HttpSession;

public final class SessionUtil2 {
  private SessionUtil2(){}

  public static void setIdleTimeoutMinutes(HttpSession session, int minutes) {
    if (minutes <= 0) return;
    session.setMaxInactiveInterval(minutes * 60);
  }
}
