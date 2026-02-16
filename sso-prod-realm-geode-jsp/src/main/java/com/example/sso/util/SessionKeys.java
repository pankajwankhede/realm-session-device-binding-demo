
package com.example.sso.util;

public final class SessionKeys {
  private SessionKeys(){}

  public static final String REALM = "REALM";
  public static final String HOME_DTO = "HOME_DTO";

  // OAuth-ish request storage: multi-tab safe
  public static final String AUTHZ_REQ_MAP = "AUTHZ_REQ_MAP";
  public static final String LAST_FLOW_KEY = "LAST_FLOW_KEY";

  // Device binding
  public static final String DEVICE_KEY = "DEVICE_KEY";

  // Concurrent login
  public static final String CURRENT_LOGIN_ID = "CURRENT_LOGIN_ID";
}
