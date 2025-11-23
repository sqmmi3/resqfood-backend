package be.ucll.config.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expirationTime;

  public String generateToken(String username) {
    return Jwts.builder()
             .setSubject(username)
             .setIssuedAt(new Date())
             .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
             .signWith(getKey(), SignatureAlgorithm.HS256)
             .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parserBuilder()
             .setSigningKey(getKey())
             .build()
             .parseClaimsJws(token)
             .getBody()
             .getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isExpired(token);
  }

  private boolean isExpired(String token) {
    return Jwts.parserBuilder()
             .setSigningKey(getKey())
             .build()
             .parseClaimsJws(token)
             .getBody()
             .getExpiration()
             .before(new Date());
  }

  private Key getKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
}
