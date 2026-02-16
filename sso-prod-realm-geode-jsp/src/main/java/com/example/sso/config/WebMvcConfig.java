
package com.example.sso.config;

import com.example.sso.interceptor.DeviceBindingInterceptor;
import com.example.sso.interceptor.SessionValidationInterceptor;
import com.example.sso.interceptor.SsoAuthenticateInterceptor;
import com.example.sso.security.RealmResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final RealmResolver realmResolver;

  public WebMvcConfig(RealmResolver realmResolver) {
    this.realmResolver = realmResolver;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(new SsoAuthenticateInterceptor(realmResolver))
      .addPathPatterns("/ssoauthenticate");

    registry.addInterceptor(new DeviceBindingInterceptor(realmResolver))
      .addPathPatterns("/**")
      .excludePathPatterns("/login", "/auth", "/logout", "/error", "/css/**", "/js/**", "/images/**", "/favicon.ico");

    registry.addInterceptor(new SessionValidationInterceptor(List.of(
      "/login", "/auth", "/logout",
      "/ssoauthenticate", "/oauth/continue", "/password-change", "/mfa",
      "/error", "/css/**", "/js/**", "/images/**", "/favicon.ico"
    )))
    .addPathPatterns("/**");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/viewjsp/resources/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/viewjsp/resources/js/");
    registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/viewjsp/resources/images/");
  }
}
