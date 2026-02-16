
package com.example.sso.service.impl;

import com.example.sso.dto.AuthzRequest;
import com.example.sso.service.OauthProvider;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class BcOauth2Provider implements OauthProvider {

  @Override
  public String providerId() { return "BC_OAUTH2"; }

  @Override
  public String buildRedirect(AuthzRequest req, String loginHintJwt) {
    // In real: call third-party /authorize endpoint and let it redirect to client
    String redirectUrl = req.getRedirectUrl();
    String sep = redirectUrl.contains("?") ? "&" : "?";
    return redirectUrl + sep + "code=demo-bc-code&state=" + enc(req.getState());
  }

  private String enc(String s) {
    return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
  }
}
