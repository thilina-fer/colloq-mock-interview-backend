package lk.ijse.springbootbackend.config;

import lk.ijse.springbootbackend.util.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults()) // CORS enable කිරීම
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Auth API (Login, Register) - No Token Needed
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. Admin APIs - ADMIN ලට විතරයි අවසර දෙන්නේ
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 3. Candidate & Interviewer APIs - අදාළ Role එක හෝ Admin ට අවසර ඇත
                        .requestMatchers("/api/v1/candidate/**").hasAnyRole("CANDIDATE", "ADMIN")
                        .requestMatchers("/api/v1/interviewer/**").hasAnyRole("INTERVIEWER", "ADMIN")

                        // 4. අනෙකුත් සියලුම API calls වලට ලොග් වී සිටීම අනිවාර්යයි
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // JWT Filter එක UsernamePasswordAuthenticationFilter එකට කලින් ක්‍රියාත්මක කරනවා
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS Configuration එක React App එක (Port 5173) සමඟ වැඩ කිරීමට
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}