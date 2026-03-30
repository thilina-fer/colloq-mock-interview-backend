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
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Endpoints (ලොගින් සහ සයින්අප්)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. Levels Endpoints
                        .requestMatchers("/api/v1/levels/**").authenticated()
                        .requestMatchers("/api/v1/levels/**").hasAuthority("ADMIN")

                        // 3. Interviewer Endpoints 🚀
                        // /all එකට CANDIDATE ටත් අවසර දිය යුතුයි (Modal එකේ පෙන්වීමට)
                        .requestMatchers("/api/v1/interviewer/all").hasAnyAuthority("CANDIDATE", "ADMIN", "INTERVIEWER")
                        // අනිත් interviewer endpoints (profile update/delete) අදාළ අයට පමණි
                        .requestMatchers("/api/v1/interviewer/**").hasAnyAuthority("INTERVIEWER", "ADMIN")

                        // 4. Availability & Booking Endpoints
                        .requestMatchers("/api/v1/availability/**").hasAnyAuthority("INTERVIEWER", "ADMIN", "CANDIDATE")
                        .requestMatchers("/api/v1/candidate/**").hasAnyAuthority("CANDIDATE", "ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

                        // 5. Any other request
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS Configuration - React App එක (Port 5173) සඳහා
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