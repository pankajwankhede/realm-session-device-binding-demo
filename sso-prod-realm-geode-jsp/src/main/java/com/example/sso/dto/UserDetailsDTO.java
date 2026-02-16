
package com.example.sso.dto;

import java.io.Serializable;

public class UserDetailsDTO implements Serializable {
  private String userName;
  private String realm;
  private boolean locked;
  private int passwordExpiresInDays;

  public String getUserName() { return userName; }
  public void setUserName(String userName) { this.userName = userName; }

  public String getRealm() { return realm; }
  public void setRealm(String realm) { this.realm = realm; }

  public boolean isLocked() { return locked; }
  public void setLocked(boolean locked) { this.locked = locked; }

  public int getPasswordExpiresInDays() { return passwordExpiresInDays; }
  public void setPasswordExpiresInDays(int passwordExpiresInDays) { this.passwordExpiresInDays = passwordExpiresInDays; }
}
