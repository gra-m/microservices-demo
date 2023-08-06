package com.microservices.demo.elastic.query.service.config;

/**
 * Set ENCRYPT_KEY environment variable to enable this locally OR include key in bootstrap.yml
 *
 * <p>Allows access to RestApi endpoints if user
 * My First Take (MFT== not researched == all prior comments!) --> Difference with config-server
 * SecurityConfig.class is @EnableWebSecurity as opposed to enabling internal library endpoints for encrypt/decrypt</p>
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {

    http
    .httpBasic()
    .and()
    .authorizeRequests()
    .antMatchers("/**").hasRole("USER")
    .and()
    .csrf().disable();

/*// to quickly permit all:
    http
    .authorizeRequests()
        .antMatchers("/**").permitAll();*/
  }

/**
 * noop sets password as plain text
 * @param auth
 * @throws Exception
 */
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth.inMemoryAuthentication()
    .withUser("test")
    .password("{noop}test1234").roles("USER");

  }
}