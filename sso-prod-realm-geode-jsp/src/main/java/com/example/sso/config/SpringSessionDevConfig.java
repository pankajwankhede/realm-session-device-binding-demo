
package com.example.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Dev-only in-memory sessions so the project runs out-of-the-box.
 * In prod, remove this and use spring-session-data-geode (or redis) SessionRepository.
 */
@Configuration
@EnableSpringHttpSession
public class SpringSessionDevConfig {

  @Bean
  public SessionRepository<?> sessionRepository() {
    return new MapSessionRepository(new ConcurrentHashMap<>());
  }
}
