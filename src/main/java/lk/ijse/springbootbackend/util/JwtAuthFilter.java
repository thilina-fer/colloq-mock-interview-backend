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

    /*@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Authorization Header එක පරීක්ෂා කිරීම
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String username;

        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
            System.out.println("❌ [JWT FILTER ERROR]: Could not extract username: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 2. දැනටමත් Authenticate වෙලා නැතිනම් පමණක් ඉදිරියට යන්න
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(jwtToken)) {

                // 💡 Token එකෙන් Role එක Extract කරගැනීම
                String role = jwtUtil.extractRole(jwtToken);
                System.out.println("🔍 [FILTER DEBUG] Token Username: " + username);
                System.out.println("🔍 [FILTER DEBUG] Extracted Role: [" + role + "]");

                // 💡 DB එකෙන් User ව හොයාගැනීම
                Auth auth = authRepo.findByUsername(username)
                        .or(() -> authRepo.findByEmail(username))
                        .orElse(null);

                if (auth != null) {
                    // Role එක "ADMIN", "CANDIDATE" වගේ පිරිසිදුව තිබිය යුතුයි
                    if (role != null && !role.isEmpty()) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                        System.out.println("✅ [FILTER SUCCESS] Setting Authority: " + authority.getAuthority());

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        auth,
                                        null,
                                        Collections.singletonList(authority)
                                );

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Security Context එකට අවසන් අවසරය ලබාදීම
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        System.out.println("⚠️ [FILTER WARN] Role is NULL or Empty for user: " + username);
                    }
                } else {
                    System.out.println("❌ [FILTER ERROR] User not found in database: " + username);
                }
            }
        }

        filterChain.doFilter(request, response);
    }*/

    // ... පරණ imports ...

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
            // 💡 Token එක Valid ද කියලත් මෙතනම check කරන එක හොඳයි
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtUtil.validateToken(jwtToken)) {
                    String role = jwtUtil.extractRole(jwtToken);

                    // 🎯 [FIX] Role එක Uppercase කරන්න (උදා: candidate -> CANDIDATE)
                    if (role != null) {
                        role = role.toUpperCase().trim();
                    }

                    Auth auth = authRepo.findByUsername(username)
                            .or(() -> authRepo.findByEmail(username))
                            .orElse(null);

                    if (auth != null && role != null && !role.isEmpty()) {
                        // 💡 SecurityConfig එකේ තියෙන නමටම (CANDIDATE) සමාන වෙනවා දැන්
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

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
                        System.out.println("✅ [JWT FILTER] Authenticated user: " + username + " with role: " + role);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ [JWT FILTER ERROR]: " + e.getMessage());
            // මෙතනදී return වෙන්න එපා, filterChain එක දිගටම යවන්න
        }

        filterChain.doFilter(request, response);
    }
}