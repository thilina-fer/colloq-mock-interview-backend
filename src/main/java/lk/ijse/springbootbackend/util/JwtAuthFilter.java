package lk.ijse.springbootbackend.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthRepo authRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String username;

        try {
            username = jwtUtil.extractUsername(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwtToken)) {
                    // Token එකෙන් එන Role එක කෙලින්ම ගන්න (උදා: "ADMIN")
                    String rawRole = jwtUtil.extractRole(jwtToken);

                    Auth auth = authRepo.findByUsername(username)
                            .or(() -> authRepo.findByEmail(username))
                            .orElse(null);

                    if (auth != null && rawRole != null) {
                        // 🎯 [CHANGE]: ROLE_ prefix එක එකතු නොකර පිරිසිදුව ලබා දීම
                        String formattedRole = rawRole.toUpperCase().trim();
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(formattedRole);

                        UserDetails userDetails = User.builder()
                                .username(auth.getUsername())
                                .password(auth.getPassword() != null ? auth.getPassword() : "")
                                .authorities(Collections.singletonList(authority))
                                .build();

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        System.out.println("✅ [JWT FILTER] Authenticated: " + username + " | Authority: " + formattedRole);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ [JWT FILTER ERROR]: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}