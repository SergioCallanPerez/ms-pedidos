package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 1. Deshabilita CSRF
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // 2. Habilita CORS y usa el Bean de configuración CorsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 3. Configuración de autorización
                .authorizeExchange(exchanges -> exchanges
                        // [A] PERMITIR OPTIONS
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // [B] PERMITIR RUTAS DE PRODUCTO
                        .pathMatchers("/api/**").permitAll()

                        // [C] CUALQUIER OTRA RUTA: Requerir autenticación.
                        .anyExchange().authenticated()
                )

                // 4. Deshabilita la autenticación por defecto (para evitar la contraseña generada)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .build();
    }
}
