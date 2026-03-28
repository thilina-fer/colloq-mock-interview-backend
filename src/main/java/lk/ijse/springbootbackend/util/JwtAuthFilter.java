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
import java.util.List;

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
            // Token එක කියවන්න බැරි නම් filter chain එක දිගටම යවන්න
            filterChain.doFilter(request, response);
            return;
        }

        // 2. දැනටමත් Authenticate වෙලා නැතිනම් පමණක් ඉදිරියට යන්න
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 💡 Token එක Validate කර බලන්න
            if (jwtUtil.validateToken(jwtToken)) {

                // 💡 Token එක ඇතුළෙන්ම Role එක extract කරගන්න (මම කලින් JwtUtil එකට දුන්න method එක)
                String role = jwtUtil.extractRole(jwtToken);

                // 💡 DB එකෙන් අදාළ User ව හොයාගන්න (Principal object එක විදිහට තියාගන්න)
                Auth auth = authRepo.findByUsername(username)
                        .or(() -> authRepo.findByEmail(username))
                        .orElse(null);

                if (auth != null) {
                    // 💡 Spring Security වලට අදාළ "ROLE_" prefix එක සමඟ Authority එක හැදීම
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    auth, // Principal (User object)
                                    null,
                                    Collections.singletonList(authority) // Authorities list
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 💡 අවසාන වශයෙන් Security Context එකට Authentication එක ඇතුළත් කිරීම
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}