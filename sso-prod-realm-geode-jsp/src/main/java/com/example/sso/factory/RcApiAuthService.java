
package com.example.sso.factory;

import com.example.sso.dto.UserDetailsDTO;
import com.example.sso.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * Stub for RC external auth API.
 * Replace with real RestClient/WebClient call.
 */
@Service("rcApiAuthService")
public class RcApiAuthService implements AuthService {

  @Override
  public AuthResult authenticate(String realm, String username, String password) {
    if (!"pass".equals(password)) return new AuthResult(false, "RC API: invalid credentials", null);

    UserDetailsDTO u = new UserDetailsDTO();
    u.setRealm(realm);
    u.setUserName(username);
    u.setLocked(false);
    u.setPasswordExpiresInDays(90);
    return new AuthResult(true, "OK", u);
  }
}
