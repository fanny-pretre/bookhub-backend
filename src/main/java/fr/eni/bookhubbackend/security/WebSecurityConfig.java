package fr.eni.bookhubbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Active CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Règles de sécurité
                .authorizeHttpRequests(auth -> auth

                        // AUTH
                        .requestMatchers("/api/auth/**").permitAll()

                        // SWAGGER
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // LIVRES
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/{id}", "/api/books/search").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/books").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/{isbn}").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/{isbn}").hasAnyRole("LIBRARIAN", "ADMIN")

                        // EMPRUNTS
                        .requestMatchers(HttpMethod.GET, "/api/loans/my/{userId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/loans").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/loans/{id}").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/loans").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/loans/{id}/return").hasAnyRole("LIBRARIAN", "ADMIN")

                        // RÉSERVATIONS
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/{id}").hasAnyRole("LIBRARIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/my").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservations").authenticated().requestMatchers(HttpMethod.DELETE, "/api/reservations/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservations/{id}/validate").hasAnyRole("LIBRARIAN", "ADMIN")

                        // UTILISATEURS
                        .requestMatchers("/api/users/{id}").authenticated()
                        .requestMatchers("/api/users/{id}/password").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("LIBRARIAN", "ADMIN")

                        // PAR DEFAUT
                        .anyRequest().authenticated()
                )

                // Filtre JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Autorise les requêtes venant du front Angular en développement
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        // Méthodes HTTP autorisées
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Autorise tous les headers (dont Authorization pour le JWT plus tard)
        config.setAllowedHeaders(List.of("*"));
        // Autorise l'envoi des cookies et du token JWT
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applique cette config à toutes les routes /api/**
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        //return NoOpPasswordEncoder.getInstance(); //sans gestion du chiffrement
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
