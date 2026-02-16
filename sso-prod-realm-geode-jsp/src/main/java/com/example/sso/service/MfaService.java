
package com.example.sso.service;

public interface MfaService {
  boolean startMfa(String realm, String username);
  boolean verify(String realm, String username, String otp);
}
