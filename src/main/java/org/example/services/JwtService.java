package org.example.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.example.interfaces.IJwtService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService {
    private  final String JWT_SECRET = "aGFzaC1zZWNyZXQtZm9yLWp3dC1hcGktc3RhbmRhcmQ=";
    private final long JWT_EXPIRATION = 604800000L;  // 7 днів

    // Генерація токена
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        if (user != null) {
            claims.put("id", user.getId());
            claims.put("email", user.getEmail());
            claims.put("roles", user.getRoles().stream().map(UserRole::getName).toArray());
            claims.put("name", user.getName());
            claims.put("surname", user.getSurname());
            claims.put("image", user.getImage());
            claims.put("username", user.getUsername());
            claims.put("birthdate", user.getBirthdate());
        }
        assert user != null;
        return generateToken(claims, user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(),Jwts.SIG.HS256)
                .compact();
    }


    // Отримання ім'я користувача з токена
    public String extractUserName(String token) {
        Claims claims = extractAllClaims(token);
        if(claims != null){
            return claims.getSubject();
        }
        else{
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        if(claims != null){
            return claimsResolvers.apply(claims);
        }
        else{
            return null;
        }
    }

    // Валідація токена
    public  boolean isTokenValid(String token, User user) {
        final String userName = extractUserName(token);
        return (userName.equals(user.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ignored) {}
        return claims;
    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

}
