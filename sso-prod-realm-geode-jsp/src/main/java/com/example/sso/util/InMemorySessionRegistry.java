
package com.example.sso.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemorySessionRegistry implements SessionRegistry {

  private final Map<String, String> map = new ConcurrentHashMap<>();

  private String key(String realm, String username) {
    return realm + "|" + username;
  }

  @Override
  public String getSessionId(String realm, String username) {
    return map.get(key(realm, username));
  }

  @Override
  public void putSessionId(String realm, String username, String sessionId) {
    map.put(key(realm, username), sessionId);
  }

  @Override
  public void removeSessionId(String realm, String username) {
    map.remove(key(realm, username));
  }
}
