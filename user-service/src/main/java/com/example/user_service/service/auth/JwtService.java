package com.example.user_service.service.auth;

import com.example.user_service.entity.User;
import com.example.user_service.service.interfaces.UserRoleService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.secret_key}")
    private String secretKey;
    @Value("${security.jwt.access_token_expiration}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;
    private final UserRoleService userRoleService;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(User user, long expiryTime) {
        List<String> roles = userRoleService.getAllUserRoles(user);
        JwtBuilder builder = Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey());
        return builder.compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<SimpleGrantedAuthority> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class)
                .stream().map(ur -> new SimpleGrantedAuthority(String.format("ROLE_%s", ur))).toList();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isAccessTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isValid(String token) {
        try {
            if (isAccessTokenExpired(token)) {
                log.info("Token has expired");
                return false;
            }
            return true;
        } catch (ExpiredJwtException ex) {
            log.info("Expired token");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | SecurityException |
                 IllegalArgumentException ex) {
            log.warn("Invalid token: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("ERROR", ex);
        }
        return false;
    }

    public boolean isValidRefresh(String token) {
        return isValid(token);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.get("name", String.class);
        Collection<? extends GrantedAuthority> authorities = extractRoles(token);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
