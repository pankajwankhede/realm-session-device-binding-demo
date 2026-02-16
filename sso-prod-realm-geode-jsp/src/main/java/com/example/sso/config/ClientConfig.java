
package com.example.sso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "")
public class ClientConfig {

  // bound from client-config.yml using @PropertySource in a config below
  private Map<String, ClientPolicy> clients;
  private Map<String, CrossRealmPolicy> crossRealm;

  public Map<String, ClientPolicy> getClients() { return clients; }
  public void setClients(Map<String, ClientPolicy> clients) { this.clients = clients; }

  public Map<String, CrossRealmPolicy> getCrossRealm() { return crossRealm; }
  public void setCrossRealm(Map<String, CrossRealmPolicy> crossRealm) { this.crossRealm = crossRealm; }

  public ClientPolicy policy(String realm) {
    if (clients == null) return null;
    return clients.get(realm);
  }

  public CrossRealmPolicy crossPolicy(String realm) {
    if (crossRealm == null) return null;
    return crossRealm.get(realm);
  }

  public static class ClientPolicy {
    private Features features;
    private SessionPolicy sessionPolicy;
    private String oauthProvider;

    public Features getFeatures() { return features; }
    public void setFeatures(Features features) { this.features = features; }

    public SessionPolicy getSessionPolicy() { return sessionPolicy; }
    public void setSessionPolicy(SessionPolicy sessionPolicy) { this.sessionPolicy = sessionPolicy; }

    public String getOauthProvider() { return oauthProvider; }
    public void setOauthProvider(String oauthProvider) { this.oauthProvider = oauthProvider; }
  }

  public static class Features {
    private boolean forgetUsername;
    private boolean forgetPassword;
    private boolean mfa;
    private boolean passwordExpiryRedirect;

    public boolean isForgetUsername() { return forgetUsername; }
    public void setForgetUsername(boolean forgetUsername) { this.forgetUsername = forgetUsername; }

    public boolean isForgetPassword() { return forgetPassword; }
    public void setForgetPassword(boolean forgetPassword) { this.forgetPassword = forgetPassword; }

    public boolean isMfa() { return mfa; }
    public void setMfa(boolean mfa) { this.mfa = mfa; }

    public boolean isPasswordExpiryRedirect() { return passwordExpiryRedirect; }
    public void setPasswordExpiryRedirect(boolean passwordExpiryRedirect) { this.passwordExpiryRedirect = passwordExpiryRedirect; }
  }

  public static class SessionPolicy {
    private int idleTimeoutMinutes;
    private int absoluteMaxHours;
    private String concurrentLogin; // ALLOW, BLOCK, INVALIDATE_OLD

    public int getIdleTimeoutMinutes() { return idleTimeoutMinutes; }
    public void setIdleTimeoutMinutes(int idleTimeoutMinutes) { this.idleTimeoutMinutes = idleTimeoutMinutes; }

    public int getAbsoluteMaxHours() { return absoluteMaxHours; }
    public void setAbsoluteMaxHours(int absoluteMaxHours) { this.absoluteMaxHours = absoluteMaxHours; }

    public String getConcurrentLogin() { return concurrentLogin; }
    public void setConcurrentLogin(String concurrentLogin) { this.concurrentLogin = concurrentLogin; }
  }

  public static class CrossRealmPolicy {
    private List<String> allowedClients;
    private List<String> blockedClients;

    public List<String> getAllowedClients() { return allowedClients; }
    public void setAllowedClients(List<String> allowedClients) { this.allowedClients = allowedClients; }

    public List<String> getBlockedClients() { return blockedClients; }
    public void setBlockedClients(List<String> blockedClients) { this.blockedClients = blockedClients; }
  }
}
