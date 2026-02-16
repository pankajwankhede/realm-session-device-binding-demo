
package com.example.sso.service;

import com.example.sso.dto.AuthzRequest;

/**
 * Implement per-provider redirect / code exchange logic.
 * Demo returns a redirect URL that simulates code returned to redirectUrl.
 */
public interface OauthProvider {
  String providerId();
  String buildRedirect(AuthzRequest req, String loginHintJwt);
}
