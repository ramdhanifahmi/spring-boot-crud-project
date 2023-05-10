package com.dev.fahmi.common;

import com.dev.fahmi.domain.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private String jwt;
    private Date expirationDate;

    @Value("${jwt.key.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private long expirationMs;

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        this.jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        this.expirationDate = expiryDate;
        return jwt;
    }

    public String getToken(String username) {
        if (StringUtils.isEmpty(jwt)) {
            // generate a new token with the provided username
            return generateToken(username);
        } else {
            try {
                Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
                return jwt;
            } catch (JwtException ex) {
                // token is invalid, generate a new one with the provided username
                return generateToken(username);
            }
        }
    }

    public boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // This method extracts a claim from a JWT token using a function
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token); // This gets all claims from the token
        return claimsResolver.apply(claims); // This applies the function to the claims and returns the result
    }

    // This method extracts all claims from a JWT token using the secret key
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody(); // This parses and verifies the token with the secret key and returns its body as claims
    }

    // This method extracts the username from a JWT token using a function
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); // This gets the claim from the token using a function that returns its subject as username
    }
}
