
package com.example.sso.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(ClientConfig.class)
@PropertySource(value = "classpath:client-config.yml", factory = YamlPropertySourceFactory.class)
public class ClientConfigLoader {}
