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

        // Header එකක් නැත්නම් හෝ Bearer නොවෙයි නම් ඊළඟ filter එකට යවන්න
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String username;

        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
            // Token එක extract කරන්න බැරි නම් process එක නවත්තන්න
            filterChain.doFilter(request, response);
            return;
        }

        // දැනටමත් authenticate වෙලා නැතිනම් පමණක් ඉදිරියට යන්න
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Username එකෙන් හෝ Email එකෙන් User ව හොයාගන්න (Login එකට ගැලපෙන ලෙස)
            Auth auth = authRepo.findByUsername(username)
                    .or(() -> authRepo.findByEmail(username))
                    .orElse(null);

            // Token එක validate කර බලන්න
            if (auth != null && jwtUtil.validateToken(jwtToken)) {

                // වැදගත්ම කොටස: මෙතැනදී "ROLE_" prefix එක සමඟ Enum name එක ලබා දිය යුතුයි
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                auth, // String එකක් වෙනුවට Auth object එකම ලබා දෙන්න
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + auth.getRole().name()))
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // SecurityContext එකට Authentication එක ඇතුළත් කිරීම
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}