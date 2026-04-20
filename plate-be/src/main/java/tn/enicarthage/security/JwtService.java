package tn.enicarthage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    // 256-bit secret key for HS256
    private static final String SECRET_KEY = "9v9y/B?E(H+MbQeThWaZq4t7w!z%C*F-J@NcRfUjXn2r5u8x/A?D(G+KbPeShVmY"; 
    // Génération du token avec tous les claims explicites (pas de setSubject)
    public String generateToken(String username, String email, String role) {
        return Jwts.builder()
                .claim("username", username)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Surcharge pour compatibilité (utilise username comme email par défaut)
    public String generateToken(String username, String role) {
        return generateToken(username, username, role);
    }

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
