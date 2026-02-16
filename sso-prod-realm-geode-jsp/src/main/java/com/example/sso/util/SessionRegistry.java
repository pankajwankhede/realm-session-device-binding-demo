
package com.example.sso.util;

/**
 * Production: back this with Geode/Redis so it works across instances.
 * Demo: simple in-memory registry.
 */
public interface SessionRegistry {

  /**
   * @return existing sessionId (or null) for (realm, username)
   */
  String getSessionId(String realm, String username);

  void putSessionId(String realm, String username, String sessionId);

  void removeSessionId(String realm, String username);
}
