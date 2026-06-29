package in.secureauthportal.security;

import in.secureauthportal.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        // jjwt 0.12.x requires the key in bytes; using the raw UTF-8 bytes of
        // the configured secret. For production, prefer a base64-encoded
        // secret of at least 256 bits, decoded with Decoders.BASE64.
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Generates a signed JWT with the user's email as the subject and role as a claim
    public String generateToken(String email, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracts the email (subject) from a valid token
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Extracts the role claim from a valid token
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // Returns true if the token is well-formed, correctly signed, and not expired
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            // malformed, unsupported, or bad signature
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
