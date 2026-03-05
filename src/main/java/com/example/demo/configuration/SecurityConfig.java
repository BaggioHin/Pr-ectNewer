package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/products/check","/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refreshToken",
            "/admin/debug/auth","/admin/debug","/courses/{id}","/courses/search","/courses","/user",
            "/swagger-ui/**","/Subject/{id}","/Subject/{name}","/Subject/{page}",
            "/v3/api-docs/**","/users/"
    };

    @Autowired
    @Lazy
    private CustomJWTDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/consultation-requests").permitAll()
                        .requestMatchers(HttpMethod.GET, "/consultation-requests/**").hasAnyRole("SALER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/consultation-requests/**").hasAnyRole("SALER", "ADMIN")
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );

        return http.build();
    }



    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthorityPrefix("ROLE_");
        scopesConverter.setAuthoritiesClaimName("SCOPE");

//        JwtGrantedAuthoritiesConverter specialityConverter = new JwtGrantedAuthoritiesConverter();
//        specialityConverter.setAuthorityPrefix("SPECIALITY_");
//        specialityConverter.setAuthoritiesClaimName("SPECIALITY");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt->{
            var authorities = scopesConverter.convert(jwt);
            return authorities;
        });
        return jwtAuthenticationConverter;
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
