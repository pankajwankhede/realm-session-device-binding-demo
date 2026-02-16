
package com.example.sso.factory;

import com.example.sso.dto.UserDetailsDTO;
import com.example.sso.service.AuthService;
import org.springframework.stereotype.Service;

@Service("internalAuthService")
public class InternalAuthService implements AuthService {

  @Override
  public AuthResult authenticate(String realm, String username, String password) {

    if (!"pass".equals(password)) {
      return new AuthResult(false, "Invalid credentials", null);
    }

    UserDetailsDTO u = new UserDetailsDTO();
    u.setRealm(realm);
    u.setUserName(username);

    // demo policy signals
    if ("locked".equalsIgnoreCase(username)) {
      u.setLocked(true);
      return new AuthResult(false, "User locked", u);
    }
    u.setLocked(false);
    u.setPasswordExpiresInDays("expire".equalsIgnoreCase(username) ? 3 : 20);

    return new AuthResult(true, "OK", u);
  }
}
