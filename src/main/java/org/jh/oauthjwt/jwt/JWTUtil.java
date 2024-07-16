package org.jh.oauthjwt.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
//public class JWTUtil {
//
//    private SecretKey secretKey;
//
//    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
//
//
//        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
//    }
//
//    public String getUsername(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("username", String.class);
//    }
//
//    public String getRole(String token) {
//        return Jwts
//                .parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("role", String.class);
//    }
//
//    public String getEmail(String token) {
//        return Jwts
//                .parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("email", String.class);
//    }
//
//    public String getCategory(String token) {
//        return Jwts
//                .parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("category", String.class);
//    }
//
//    public Boolean isExpired(String token) {
//        return Jwts
//                .parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getExpiration()
//                .before(new Date());
//    }
//
//    public String createJwt(String category, String username, String email, String role, Long expiredMs) {
//
//        return Jwts.builder()
//                .claim("category", category)
//                .claim("username", username)
//                .claim("email", email)
//                .claim("role", role)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + expiredMs))
//                .signWith(secretKey)
//                .compact();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
//            return !isExpired(token);
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//}
@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return getClaim(token, "username");
    }

    public String getRole(String token) {
        return getClaim(token, "role");
    }

    public String getEmail(String token) {
        return getClaim(token, "email");
    }

    public String getCategory(String token) {
        return getClaim(token, "category");
    }

    private String getClaim(String token, String claimName) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(claimName, String.class);
    }

    public Boolean isExpired(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public String createJwt(String category, String username, String email, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return !isExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
