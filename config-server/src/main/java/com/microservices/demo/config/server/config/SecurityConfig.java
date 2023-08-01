package com.microservices.demo.config.server.config;

/**
 * Set ENCRYPT_KEY environment variable to enable this locally OR include key in bootstrap.yml
 *
 * <p>This class enables log-in free access to the encrypt/decrypt and /actuator/health endpoints
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers("/encrypt/**")
        .antMatchers("/decrypt/**")
        .antMatchers("/actuator/**");
    super.configure(web);
  }
}
