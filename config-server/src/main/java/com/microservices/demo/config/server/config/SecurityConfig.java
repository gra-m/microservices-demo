package com.microservices.demo.config.server.config;

/**
 * Set ENCRYPT_KEY environment variable to enable this locally OR include key in bootstrap.yml
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring()
    .antMatchers("/encrypt/**")
    .antMatchers("/decrypt/**");
    super.configure(web);
}



}
