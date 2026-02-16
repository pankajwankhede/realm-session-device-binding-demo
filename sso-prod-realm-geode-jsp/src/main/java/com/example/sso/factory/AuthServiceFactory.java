
package com.example.sso.factory;

import com.example.sso.service.AuthService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthServiceFactory {

  private final Map<String, AuthService> services;

  public AuthServiceFactory(Map<String, AuthService> services) {
    this.services = services;
  }

  /**
   * Mapping:
   * - "internalAuthService" -> internal
   * - "rcApiAuthService" -> RC external API
   *
   * In prod you can map by realm using ClientConfig.
   */
  public AuthService get(String beanName) {
    return services.get(beanName);
  }
}
