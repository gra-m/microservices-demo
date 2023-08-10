package com.microservices.demo.elastic.query.web.client.config;

import com.microservices.demo.config.UserConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

private final UserConfigData userConfigData;

public WebSecurityConfig(UserConfigData userConfigData) {
    this.userConfigData = userConfigData;
}


/**
 *  Currently temp basic implementation that allows anyone with USER role, fully requeste allows for 'Remember Me'
 *  functionalilty. Log out will not be implemented for now.
 * @param httpSecurity
 * @throws Exception
 */
@Override
protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
    .httpBasic()
    .and()
    .authorizeRequests()
    .antMatchers("/").permitAll()
    .antMatchers("/**").hasRole("USER")
    .anyRequest()
    .fullyAuthenticated();
}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
    .inMemoryAuthentication()
    .withUser(userConfigData.getUsername())
    .password(passwordEncoder().encode(userConfigData.getPassword())) // inMemory pword not in plaintext
    .roles(userConfigData.getRoles());
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

}
