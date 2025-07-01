package com.wolfstreet.security_lib.service;

import com.wolfstreet.security_lib.details.JwtDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).get("name", String.class);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public List<SimpleGrantedAuthority> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class)
                .stream().map(ur -> new SimpleGrantedAuthority(String.format("ROLE_%s", ur))).toList();
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

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String userId = extractUserId(token);
        String username = extractUserName(token);
        Collection<SimpleGrantedAuthority> authorities = extractRoles(token);
        JwtDetails jwtDetails = new JwtDetails(Long.valueOf(userId), username, authorities);
        return new UsernamePasswordAuthenticationToken(jwtDetails, null, authorities);
    }

    private boolean isAccessTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
} 