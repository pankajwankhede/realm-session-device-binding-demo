
package com.example.sso.factory;

import com.example.sso.service.OauthProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OauthProviderFactory {

  private final List<OauthProvider> providers;

  public OauthProviderFactory(List<OauthProvider> providers) {
    this.providers = providers;
  }

  public OauthProvider byId(String id) {
    if (id == null) return null;
    return providers.stream().filter(p -> id.equals(p.providerId())).findFirst().orElse(null);
  }
}
