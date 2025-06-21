package com.banking.OnlineBanking.config;

import com.common.BankData.dao.CustomerDao;
import com.common.BankData.service.AuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/*
The SecurityConfig.java file defines the main Spring Security configuration for your banking application. Its main role is to:

- Configure which URLs are public and which require authentication.
- Register a custom authentication provider for validating user credentials.
- Register the custom AuthenticationFilter to handle JWT/Bearer token authentication for protected endpoints.
- Enforce stateless session management (no server-side sessions).
- Disable CSRF, form login, HTTP Basic, and logout for API security.
- Set up password encoding using BCrypt for secure password storage.

In summary, this configuration ensures that only authenticated users with valid tokens can access protected resources, while allowing public access to specified endpoints, and applies best practices for securing RESTful APIs.
*/
/*
- SecurityConfig.java secures endpoints and manages authentication/authorization.
- RequestFilter.java manages cross-origin access and CORS headers for frontend-backend communication.  
They work together to secure your application at different layers: one for authentication/authorization, the other for cross-origin request handling.
 */
@Configuration
@EnableWebSecurity
@EnableJpaRepositories(basePackageClasses = CustomerDao.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider provider;

    private static final String[] PUBLIC_URLS = {
        "/webjars/**",
        "/css/**",
        "/js/**",
        "/images/**",
        "/",
        "/about/**",
        "/contact/**",
        "/error/**/*",
        "/console/**",
        "/signup",
        "/login/**",
        "**/secured/**"
    };

    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(
        new AntPathRequestMatcher("/accounts/**")
    );

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    AuthenticationFilter authenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(PROTECTED_URLS);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }

    @Override
    public void configure(final WebSecurity webSecurity) {
        webSecurity.ignoring()
                   .antMatchers(PUBLIC_URLS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(provider)
            .addFilterBefore(authenticationFilter(), AnonymousAuthenticationFilter.class)
            .authorizeRequests()
            .requestMatchers(PROTECTED_URLS)
            .authenticated()
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .logout().disable();
    }
}