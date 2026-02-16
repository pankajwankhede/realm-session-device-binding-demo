
package com.example.sso.service;

import org.springframework.stereotype.Service;

/**
 * Hooks only. Wire your MFA SDK(s) here (BC uses SDK hook, IHS uses their API, etc.)
 */
@Service
public class StubMfaService implements MfaService {

  @Override
  public boolean startMfa(String realm, String username) {
    return true;
  }

  @Override
  public boolean verify(String realm, String username, String otp) {
    return otp != null && !otp.isBlank();
  }
}
