package org.com.pet_spr.configuration;


import lombok.RequiredArgsConstructor;
import org.com.pet_spr.security.jwt.JwtCustomAuthenticationEntryPoint;
import org.com.pet_spr.security.jwt.JwtPreFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    private final JwtPreFilter jwtPreFilter;
    private final JwtCustomAuthenticationEntryPoint jwtCustomAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->{

                    auth.requestMatchers("/api/v1/auth/**", "/api/v1/forget-password/**").permitAll();
                    auth.anyRequest().authenticated();

                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtCustomAuthenticationEntryPoint))
                .addFilterBefore(jwtPreFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
