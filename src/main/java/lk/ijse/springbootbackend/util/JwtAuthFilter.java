package lk.ijse.springbootbackend.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j // 💡 Logging පාවිච්චි කිරීම ලෙඩ අල්ලන්න ලේසියි
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthRepo authRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Header එකක් නැත්නම් හෝ Bearer නොවෙයි නම්, ඊළඟ filter එකට යවන්න (permitAny endpoints සඳහා)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwtToken = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(jwtToken);

            // 2. Username එක තියෙනවා නම් සහ දැනටමත් Authenticate වෙලා නැත්නම් පමණක් ඇතුළට යන්න
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtUtil.validateToken(jwtToken)) {

                    String role = jwtUtil.extractRole(jwtToken);

                    // 💡 Username හෝ Email එකෙන් User ව හොයමු
                    Auth auth = authRepo.findByUsername(username)
                            .or(() -> authRepo.findByEmail(username))
                            .orElse(null);

                    // 3. User ඉන්නවා නම්, Role එක තියෙනවා නම් සහ Status එක ACTIVE නම් පමණක් Auth එක දෙන්න
                    if (auth != null && role != null && "ACTIVE".equalsIgnoreCase(String.valueOf(auth.getStatus()))) {

                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        auth,
                                        null,
                                        Collections.singletonList(authority)
                                );

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.info("User {} authenticated with role {}", username, role);
                    } else {
                        log.warn("Authentication failed for user: {}. Status might not be ACTIVE or Role is null.", username);
                    }
                }
            }
        } catch (Exception e) {
            // 💡 මොකක් හරි Error එකක් වුණොත් Server එක Crash වෙන්න නොදී Log එකක් දාන්න
            log.error("JWT Authentication Error: {}", e.getMessage());
        }

        // 4. හැම වෙලාවකම Filter Chain එක ඉදිරියට ගෙන යාම අනිවාර්යයි
        filterChain.doFilter(request, response);
    }
}