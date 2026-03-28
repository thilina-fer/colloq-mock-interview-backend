package lk.ijse.springbootbackend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.expiration}")
    private long expiration;
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(String username, String role) { // 💡 Role එකත් ගන්න
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // 💡 Role එක Claim එකක් විදිහට ඇතුළත් කරනවා
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // 💡 Token එකෙන් Role එක extract කරගන්න මේ method එකත් ඇඩ් කරන්න
    public String extractRole(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public String extractUsername(String token){
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
