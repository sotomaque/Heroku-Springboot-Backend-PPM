package com.sotomaque.ppmtool.security;

import com.sotomaque.ppmtool.domain.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.sotomaque.ppmtool.security.SecurityConstants.EXPIRATION_TIME;
import static com.sotomaque.ppmtool.security.SecurityConstants.SECRET;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// method that will generate token when we have a valid username and password

@Component
public class JwtTokenProvider {
    // generate token
    public String generateToken(Authentication authentication) {
        // get user
        User user = (User) authentication.getPrincipal();
        // get date with MS
        Date now = new Date(System.currentTimeMillis());
        // get expiration date
        Date expiryDate = new Date(now.getTime()+EXPIRATION_TIME);

        // get userId
        String userId = Long.toString(user.getId());

        // this is what we can return to the client
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());

        // return JWTsBuilder to start building JsonWebToken
        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    // validate token
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            System.out.println("Invalid JWT Signature");
        }catch (MalformedJwtException ex){
            System.out.println("Invalid JWT Token");
        }catch (ExpiredJwtException ex){
            System.out.println("Expired JWT token");
        }catch (UnsupportedJwtException ex){
            System.out.println("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            System.out.println("JWT claims string is empty");
        }
        return false;
    }

    // get userId from token (with CustomUserService)
    // extracting it from claims
    public Long getUserIdFromJWT(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");

        return Long.parseLong(id);
    }
}
