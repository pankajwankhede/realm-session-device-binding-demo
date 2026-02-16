
package com.example.sso.config;

import com.example.sso.security.RealmResolver;
import com.example.sso.security.RealmWiseCookieCsrfTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
public class SecurityConfig {

  @Bean
  public CsrfTokenRepository csrfTokenRepository(RealmResolver realmResolver) {
    return new RealmWiseCookieCsrfTokenRepository(realmResolver);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, CsrfTokenRepository csrfRepo) throws Exception {
    return http
      .csrf(csrf -> csrf.csrfTokenRepository(csrfRepo))
      .sessionManagement(sm -> sm.sessionFixation(sf -> sf.migrateSession()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
        .requestMatchers("/login", "/auth", "/logout", "/ssoauthenticate", "/oauth/continue",
                         "/password-change", "/mfa", "/error").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(form -> form.disable())
      .httpBasic(Customizer.withDefaults())
      .build();
  }
}
