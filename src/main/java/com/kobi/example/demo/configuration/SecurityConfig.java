package com.kobi.example.demo.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@Getter(AccessLevel.PACKAGE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final static String ADMIN = "ADMIN";

    @Value("${notifier.username:admin}")
    private String username;

    @Value("${notifier.password:admin}")
    private String password;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/v1/update").hasAnyRole(ADMIN)
                    .antMatchers("/v1/data").permitAll()
                .and()
                    .httpBasic()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new BasicAuthenticationEntryPoint());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(username).password("{noop}"+password).roles(ADMIN);
    }





}
