
package com.example.sso.controller;

import com.example.sso.config.ClientConfig;
import com.example.sso.dto.AuthzRequest;
import com.example.sso.dto.HomeDTO;
import com.example.sso.dto.LoginVO;
import com.example.sso.factory.AuthServiceFactory;
import com.example.sso.util.CurrentLoginUtil;
import com.example.sso.util.SessionKeys;
import com.example.sso.util.SessionRegistry;
import com.example.sso.util.SessionUtil;
import com.example.sso.util.SessionUtil2;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class AuthController {

  private final ClientConfig clientConfig;
  private final AuthServiceFactory authServiceFactory;
  private final SessionRegistry sessionRegistry;

  public AuthController(ClientConfig clientConfig, AuthServiceFactory authServiceFactory, SessionRegistry sessionRegistry) {
    this.clientConfig = clientConfig;
    this.authServiceFactory = authServiceFactory;
    this.sessionRegistry = sessionRegistry;
  }

  @GetMapping("/ssoauthenticate")
  public String entry(HttpServletRequest request) {
    // Interceptor stored request; if already authenticated, continue
    HttpSession session = request.getSession(false);
    if (session != null) {
      HomeDTO home = (HomeDTO) session.getAttribute(SessionKeys.HOME_DTO);
      if (home != null && home.isUserAuthenticated()) return "redirect:/oauth/continue";
    }
    return "redirect:/login";
  }

  @GetMapping("/login")
  public String loginPage(HttpServletRequest request, Model model,
                          @RequestParam(required = false) String state,
                          @RequestParam(required = false) String realm) {

    HttpSession session = request.getSession(true);

    String r = StringUtils.hasText(realm) ? realm.trim().toUpperCase()
            : (String) session.getAttribute(SessionKeys.REALM);

    if (!StringUtils.hasText(r)) r = "DEFAULT";
    session.setAttribute(SessionKeys.REALM, r);

    ClientConfig.ClientPolicy policy = clientConfig.policy(r);
    if (policy != null && policy.getFeatures() != null) {
      model.addAttribute("showForgetUsername", policy.getFeatures().isForgetUsername());
      model.addAttribute("showForgetPassword", policy.getFeatures().isForgetPassword());
    } else {
      model.addAttribute("showForgetUsername", true);
      model.addAttribute("showForgetPassword", true);
    }

    HomeDTO home = (HomeDTO) session.getAttribute(SessionKeys.HOME_DTO);
    if (home == null) {
      home = new HomeDTO();
      home.setLoginVo(new LoginVO());
      session.setAttribute(SessionKeys.HOME_DTO, home);
    } else if (home.getLoginVo() == null) {
      home.setLoginVo(new LoginVO());
    }

    model.addAttribute("homeDTO", home);
    model.addAttribute("state", state);

    return "login/login";
  }

  @PostMapping("/auth")
  public String authenticate(HttpServletRequest request,
                             @RequestParam(required = false) String state,
                             @RequestParam(required = false) String realm,
                             @ModelAttribute("homeDTO") HomeDTO formHome,
                             Model model) {

    HttpSession session = request.getSession(true);

    // Resolve realm
    String r = StringUtils.hasText(realm) ? realm.trim().toUpperCase()
            : (String) session.getAttribute(SessionKeys.REALM);
    if (!StringUtils.hasText(r)) r = "DEFAULT";
    session.setAttribute(SessionKeys.REALM, r);

    // Apply per-client idle timeout at login entry (per your requirement)
    ClientConfig.ClientPolicy policy = clientConfig.policy(r);
    if (policy != null && policy.getSessionPolicy() != null) {
      SessionUtil2.setIdleTimeoutMinutes(session, policy.getSessionPolicy().getIdleTimeoutMinutes());
    }

    // multi-tab safe authz
    Map<String, AuthzRequest> map = SessionUtil.getOrCreateAuthzMap(session);
    AuthzRequest authz = null;
    if (StringUtils.hasText(state) && map.containsKey(state)) authz = map.get(state);
    if (authz == null) {
      Object last = session.getAttribute(SessionKeys.LAST_FLOW_KEY);
      if (last instanceof String k && map.containsKey(k)) authz = map.get(k);
    }

    // Bind input
    LoginVO login = formHome != null ? formHome.getLoginVo() : null;
    String username = login != null ? login.getUserName() : null;
    String password = login != null ? login.getPassword() : null;

    if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
      model.addAttribute("error", "Username/Password required");
      return "login/login";
    }

    // Concurrent login enforcement (demo)
    if (policy != null && policy.getSessionPolicy() != null) {
      String mode = policy.getSessionPolicy().getConcurrentLogin();
      String existing = sessionRegistry.getSessionId(r, username);
      if ("BLOCK".equalsIgnoreCase(mode) && StringUtils.hasText(existing)) {
        model.addAttribute("error", "Concurrent login blocked for this client");
        return "login/login";
      }
      if ("INVALIDATE_OLD".equalsIgnoreCase(mode) && StringUtils.hasText(existing)) {
        // In prod: use Spring Session SessionRepository to delete that session id
        // Here: we just overwrite registry to simulate "killing old"
        sessionRegistry.removeSessionId(r, username);
      }
    }

    // Choose auth implementation: internal for BC/IHS, RC uses external
    String authBean = "internalAuthService";
    if ("RC".equalsIgnoreCase(r)) authBean = "rcApiAuthService";

    var authService = authServiceFactory.get(authBean);
    var result = authService.authenticate(r, username, password);

    if (!result.success()) {
      model.addAttribute("error", result.message());
      return "login/login";
    }

    // lock / password expiry policy examples
    if (result.userDetails() != null && result.userDetails().isLocked()) {
      model.addAttribute("error", "User locked");
      return "login/login";
    }

    boolean passwordExpiryRedirect = policy != null && policy.getFeatures() != null && policy.getFeatures().isPasswordExpiryRedirect();
    if (passwordExpiryRedirect && result.userDetails() != null && result.userDetails().getPasswordExpiresInDays() <= 5) {
      session.setAttribute("PWD_STATE", authz != null ? authz.getState() : state);
      return "redirect:/password-change";
    }

    // Mark session authenticated
    HomeDTO sessionHome = (HomeDTO) session.getAttribute(SessionKeys.HOME_DTO);
    if (sessionHome == null) sessionHome = new HomeDTO();

    sessionHome.setUserAuthenticated(true);
    sessionHome.setUsername(username);
    sessionHome.setUserDetails(result.userDetails());

    // Clear password
    if (sessionHome.getLoginVo() != null) sessionHome.getLoginVo().setPassword(null);

    session.setAttribute(SessionKeys.HOME_DTO, sessionHome);

    // Register current login for concurrent policy
    sessionRegistry.putSessionId(r, username, session.getId());
    session.setAttribute(SessionKeys.CURRENT_LOGIN_ID, CurrentLoginUtil.newLoginId());

    // MFA (BC only in sample policy)
    boolean mfa = policy != null && policy.getFeatures() != null && policy.getFeatures().isMfa();
    if (mfa) {
      session.setAttribute("MFA_STATE", authz != null ? authz.getState() : state);
      return "redirect:/mfa";
    }

    return StringUtils.hasText(state) ? "redirect:/oauth/continue?state=" + state : "redirect:/oauth/continue";
  }

  @GetMapping("/password-change")
  public String passwordChangePage() {
    return "password/password-change";
  }

  @GetMapping("/mfa")
  public String mfaPage() { return "mfa/mfa"; }

  @PostMapping("/logout")
  public String logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) session.invalidate();
    return "redirect:/login";
  }
}
