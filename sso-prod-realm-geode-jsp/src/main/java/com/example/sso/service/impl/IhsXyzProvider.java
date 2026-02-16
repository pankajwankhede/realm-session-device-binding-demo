
package com.example.sso.service.impl;

import com.example.sso.dto.AuthzRequest;
import com.example.sso.service.OauthProvider;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class IhsXyzProvider implements OauthProvider {

  @Override
  public String providerId() { return "IHS_XYZ"; }

  @Override
  public String buildRedirect(AuthzRequest req, String loginHintJwt) {
    String redirectUrl = req.getRedirectUrl();
    String sep = redirectUrl.contains("?") ? "&" : "?";
    return redirectUrl + sep + "code=demo-ihs-code&state=" + enc(req.getState());
  }

  private String enc(String s) {
    return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
  }
}
