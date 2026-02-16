
package com.example.sso.util;

import java.util.UUID;

public final class CurrentLoginUtil {
  private CurrentLoginUtil(){}

  public static String newLoginId() {
    return UUID.randomUUID().toString();
  }
}
