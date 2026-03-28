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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String username;

        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.validateToken(jwtToken)) {

                String role = jwtUtil.extractRole(jwtToken);

                Auth auth = authRepo.findByUsername(username)
                        .or(() -> authRepo.findByEmail(username))
                        .orElse(null);

                // 💡 වෙනස් කළ යුතු තැන:
                // User කෙනෙක් ඉන්නවා නම් සහ එයාගේ status එක "ACTIVE" නම් පමණක් Authentication එක දෙන්න.
                if (auth != null && "ACTIVE".equals(auth.getStatus())) {

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
                }
                // 💡 විකල්පයක් ලෙස: Status එක ACTIVE නැතිනම් මෙතනදී කෙලින්ම 403 error එකක් යවන්නත් පුළුවන්.
                // එතකොට API එකට යන්න කලින්ම Filter එකෙන් block වෙනවා.
            }
        }

        filterChain.doFilter(request, response);
    }
}