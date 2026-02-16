
package com.example.sso.controller;

import com.example.sso.config.ClientConfig;
import com.example.sso.dto.AuthzRequest;
import com.example.sso.dto.HomeDTO;
import com.example.sso.factory.OauthProviderFactory;
import com.example.sso.service.OauthProvider;
import com.example.sso.util.RedirectUtil;
import com.example.sso.util.SessionKeys;
import com.example.sso.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class Oauth2AuthController {

  private final ClientConfig clientConfig;
  private final OauthProviderFactory providerFactory;

  public Oauth2AuthController(ClientConfig clientConfig, OauthProviderFactory providerFactory) {
    this.clientConfig = clientConfig;
    this.providerFactory = providerFactory;
  }

  /**
   * Example endpoint that should require session unless excluded by interceptors.
   * If session missing, interceptor redirects to /login.
   */
  @GetMapping("/getLoginHint")
  public void getLoginHint(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpSession session = request.getSession(false);
    if (session == null) {
      response.sendRedirect(RedirectUtil.url(request, "/login"));
      return;
    }
    // demo
    response.setContentType("text/plain");
    response.getWriter().write("loginHintJwt=demo.jwt.token");
  }

  @GetMapping("/oauth/continue")
  public void continueOauth(HttpServletRequest request,
                            HttpServletResponse response,
                            @RequestParam(required = false) String state) throws Exception {

    HttpSession session = request.getSession(false);
    if (session == null) {
      response.sendRedirect(RedirectUtil.url(request, "/login"));
      return;
    }

    HomeDTO home = (HomeDTO) session.getAttribute(SessionKeys.HOME_DTO);
    if (home == null || !home.isUserAuthenticated()) {
      response.sendRedirect(RedirectUtil.url(request, "/login"));
      return;
    }

    String realm = (String) session.getAttribute(SessionKeys.REALM);
    if (!StringUtils.hasText(realm)) realm = "DEFAULT";

    Map<String, AuthzRequest> map = SessionUtil.getOrCreateAuthzMap(session);

    AuthzRequest authz = null;
    if (StringUtils.hasText(state) && map.containsKey(state)) authz = map.get(state);
    if (authz == null) {
      Object last = session.getAttribute(SessionKeys.LAST_FLOW_KEY);
      if (last instanceof String k && map.containsKey(k)) authz = map.get(k);
    }

    if (authz == null || !StringUtils.hasText(authz.getRedirectUrl())) {
      response.sendRedirect(RedirectUtil.url(request, "/login"));
      return;
    }

    ClientConfig.ClientPolicy policy = clientConfig.policy(realm);
    String providerId = policy != null ? policy.getOauthProvider() : null;

    OauthProvider provider = providerFactory.byId(providerId);
    if (provider == null) {
      response.sendError(500, "No OAuth provider configured for realm: " + realm);
      return;
    }

    // In real: build login_hint JWT etc.
    String loginHintJwt = "demo.login.hint.jwt";

    // ✅ Cleanup logic after OAuth success (remove request by state)
    // We'll compute redirect first, then cleanup.
    String redirect = provider.buildRedirect(authz, loginHintJwt);

    // cleanup state
    map.remove(authz.getState());

    response.sendRedirect(redirect);
  }
}
