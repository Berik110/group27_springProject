package com.springboot.finalproject.config;

import com.springboot.finalproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.exceptionHandling().accessDeniedPage("/403");

        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**").permitAll()
                .antMatchers("/").permitAll();

        http.formLogin()
                .loginProcessingUrl("/auth")     // <form action="/auth" method="post">
                .loginPage("/login").permitAll() // URL of Login Page
                .usernameParameter("u_email")    // <input type="email" name="u_email">
                .passwordParameter("u_password") // <input type="password" name="u_password">
                .defaultSuccessUrl("/profile")
                .failureUrl("/login?error");

        http.logout()
                .permitAll()
                .logoutUrl("/logout")            // <form action="/logout" method="post">
                .logoutSuccessUrl("/login").permitAll();

        http.csrf().disable();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
