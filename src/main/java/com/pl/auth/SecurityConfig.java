package com.pl.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler LogoutHandler;

    @Value("${cors.allowedOrigin}")
    private String allowedOrigin;

    @Value("${cors.allowedMethods}")
    private String allowedMethods;

    @Value("${cors.allowedHeaders}")
    private String allowedHeaders;

    @Value("${cors.allowedCredentials}")
    private boolean allowedCredentials;

    @Value("${cors.corsConfiguration}")
    private String corsConfiguration;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.LogoutHandler = logoutHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(corsConfiguration)
                .permitAll()

//                    .requestMatchers("/api/**")
//                    .permitAll() <--- for tests

                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**")
                .permitAll()

                .requestMatchers("/api/users/all")
                .hasAuthority(Role.ADMIN.name())

                .requestMatchers("/api/users/**", "/api/orders/**", "/api/addresses/**", "api/dishes/**", "/api/restaurants/**")
                .hasAuthority(Role.USER.name())

                .requestMatchers("/**")
                .hasAuthority(Role.ADMIN.name())

                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(LogoutHandler)
                .logoutSuccessHandler(
                        (request, response, authentication) -> SecurityContextHolder.clearContext()
                );

        return http.build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigin));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        configuration.setAllowCredentials(allowedCredentials);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsConfiguration, configuration);
        return source;
    }


}



