//package lk.ijse.springbootbackend.config;
//
//import lk.ijse.springbootbackend.util.JwtAuthFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthFilter jwtAuthFilter;
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .cors(Customizer.withDefaults())
////                .csrf(AbstractHttpConfigurer::disable)
////                .authorizeHttpRequests(auth -> auth
////                        // 💡 1. Public Endpoints (මුලින්ම තියෙන්න ඕනේ)
////                        .requestMatchers("/test/**").permitAll()
////                        .requestMatchers("/api/v1/auth/**").permitAll()
////
////                        // 💡 2. Admin Specific Endpoints (ඉහළින්ම තියන්න ලෙඩ දෙන නිසා)
////                        // මුලින්ම permitAll දාලා API එක වැඩද බලමු, ඊටපස්සේ hasAuthority දාමු
////                        .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN")
////
////                        // 💡 3. Levels Endpoints
////                        .requestMatchers(HttpMethod.GET, "/api/v1/levels/**").authenticated()
////                        .requestMatchers("/api/v1/levels/**").hasAuthority("ADMIN")
////
////                        // 💡 4. Interviewer Endpoints
////                        .requestMatchers("/api/v1/interviewer/all").hasAnyAuthority("CANDIDATE", "ADMIN", "INTERVIEWER")
////                        .requestMatchers("/api/v1/interviewer/**").hasAnyAuthority("INTERVIEWER", "ADMIN")
////
////                        // 💡 5. Other Endpoints
////                        .requestMatchers("/api/v1/availability/**").hasAnyAuthority("INTERVIEWER", "ADMIN", "CANDIDATE")
////                        .requestMatchers("/api/v1/candidate/**").hasAnyAuthority("CANDIDATE", "ADMIN")
////
////                        // 💡 6. Any other request (අන්තිමට තියෙන්න ඕනේ)
////                        .anyRequest().authenticated()
////                )
////                .sessionManagement(session -> session
////                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                )
////                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(Customizer.withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // 1. Public Endpoints
//                        .requestMatchers("/test/**", "/api/v1/auth/**").permitAll()
//
//                        // 2. Admin Specific
//                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
//
//                        // 3. Levels Endpoints
//                        // Candidate ටත් level බලන්න ඕනෙ වෙයි නේද? (Experts selection එකේදී price බලන්න)
//                        .requestMatchers(HttpMethod.GET, "/api/v1/levels/**").hasAnyAuthority("ADMIN", "CANDIDATE", "INTERVIEWER")
//                        .requestMatchers("/api/v1/levels/**").hasAuthority("ADMIN")
//
//                        // 4. Interviewer Endpoints
//                        .requestMatchers("/api/v1/interviewer/all").hasAnyAuthority("CANDIDATE", "ADMIN", "INTERVIEWER")
//                        // අනිත් interviewer endpoints (profile update වගේ) INTERVIEWER ට විතරයි
//                        .requestMatchers("/api/v1/interviewer/**").hasAnyAuthority("INTERVIEWER", "ADMIN")
//
//                        // 5. Availability Endpoints (🎯 403 එක එන්න පුළුවන් තැන)
//                        // Candidate ටත් slots බලන්න ඕනෙ නිසා මෙතන "CANDIDATE" තියෙන්නම ඕනේ
//                        .requestMatchers("/api/v1/availability/**").hasAnyAuthority("INTERVIEWER", "ADMIN", "CANDIDATE")
//
//                        // 6. Candidate Endpoints
//                        .requestMatchers("/api/v1/candidate/**").hasAnyAuthority("CANDIDATE", "ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}

package lk.ijse.springbootbackend.config;

import lk.ijse.springbootbackend.util.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(Customizer.withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // 1. Public Endpoints
//                        .requestMatchers("/test/**", "/api/v1/auth/**").permitAll()
//
//                        // 2. Admin Specific
//                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
//
//                        // 3. Levels Endpoints
//                        .requestMatchers(HttpMethod.GET, "/api/v1/levels/**").permitAll()
//                        .requestMatchers("/api/v1/levels/**").hasAuthority("ADMIN")
//
//                        // 🎯 4. Interviewer Endpoints
//                        // Profile එක complete කරන endpoint එකට ඕනෑම ලොග් වුණු කෙනෙක්ට අවසර දෙනවා (Role එක check නොකර)
//                        .requestMatchers("/api/v1/interviewer/complete-interviewer-profile").authenticated()
//                        // අනිත් interviewer endpoints වලට අදාළ roles අවශ්‍යයි
//                        .requestMatchers("/api/v1/interviewer/all").hasAnyAuthority("CANDIDATE", "ADMIN", "INTERVIEWER")
//                        .requestMatchers("/api/v1/interviewer/**").hasAnyAuthority("INTERVIEWER", "ADMIN")
//
//                        // 5. Availability Endpoints
//                        .requestMatchers("/api/v1/availability/**").hasAnyAuthority("INTERVIEWER", "ADMIN", "CANDIDATE")
//
//                        // 6. Candidate Endpoints
//                        .requestMatchers("/api/v1/candidate/**").hasAnyAuthority("CANDIDATE", "ADMIN")
//
//                        // 7. Any other request
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test/**", "/api/v1/auth/**").permitAll()

                        // 🎯 [UPDATE] Availability endpoints - හැමෝටම අවසර දෙනවා (ලොග් වෙලා ඉන්නවා නම්)
                        // මේක අනිවාර්යයෙන්ම පරීක්ෂා කරන්න
                        .requestMatchers("/api/v1/availability/**").authenticated()

                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/levels/**").permitAll()

                        // Profile complete කරන එක ඕනෑම කෙනෙක්ට authenticated වෙලා නම් පුළුවන්
                        .requestMatchers("/api/v1/interviewer/complete-interviewer-profile").authenticated()

                        .requestMatchers("/api/v1/interviewer/all").authenticated()
                        .requestMatchers("/api/v1/interviewer/**").hasAnyAuthority("INTERVIEWER", "ADMIN")

                        .requestMatchers("/api/v1/candidate/**").hasAnyAuthority("CANDIDATE", "ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

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