package org.example.interfaces;

import io.jsonwebtoken.Claims;
import org.example.entities.User;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String generateToken(User user);
    String generateToken(Map<String, Object> extraClaims, User user);
    String extractUserName(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolvers);
    boolean isTokenValid(String token, User user);
    boolean isTokenExpired(String token);
    Date extractExpiration(String token);
    Claims extractAllClaims(String token);
}
