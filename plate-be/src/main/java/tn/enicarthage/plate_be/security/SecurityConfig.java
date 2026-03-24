package tn.enicarthage.plate_be.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF Protection (désactivée pour API stateless, mais re-configurée pour token)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            // CORS Configuration - Angular uniquement
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Headers de Sécurité
            .headers(headers -> headers
                // X-Frame-Options: DENY (Clickjacking protection)
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                // X-Content-Type-Options: nosniff (MIME type sniffing protection)
                .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::and)
                // X-XSS-Protection: 1; mode=block (XSS protection header)
                .xssProtection(HeadersConfigurer.XXssProtectionConfig::and)
                // Strict-Transport-Security (HTTPS only)
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubDomains(true)
                    .preload(true)
                )
                // Content Security Policy (CSP)
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: https:; " +
                            "font-src 'self'; " +
                            "connect-src 'self' http://localhost:* https://; " +
                            "frame-ancestors 'none'; " +
                            "base-uri 'self'; " +
                            "form-action 'self'")
                )
                // Referrer-Policy
                .referrerPolicy(ref -> ref
                    .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_NO_REFERRER)
                )
                // Permissions-Policy (Feature-Policy)
                .permissionsPolicy(permissions -> permissions
                    .policy("geolocation=(), microphone=(), camera=(), payment=()"))
            )
            // Authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            // Session Management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Exception Handling
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(403);
                    response.getWriter().write("{\"error\": \"Access Denied\"}");
                })
            )
            // Authentication Provider & Filter
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration - Autorise les requêtes depuis Angular uniquement
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // URLs autorisées (adapter selon votre environnement)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",      // Local development
            "http://localhost:3000",      // Alternative port
            "https://yourdomain.com"      // Production domain
        ));
        
        // Methods autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        
        // Headers exposés au frontend
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        // Credentials (cookies, auth headers)
        configuration.setAllowCredentials(true);
        
        // Cache préflight (30 minutes)
        configuration.setMaxAge(1800L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}
