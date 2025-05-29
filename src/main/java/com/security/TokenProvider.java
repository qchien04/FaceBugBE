package com.security;


import com.constant.AccountType;
import com.constant.JwtConstant;
import com.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.lang.Throwable;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class TokenProvider {

    SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
    public String genarateToken(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt= Jwts.builder().setIssuer("Achien").setIssuedAt(new Date()).setExpiration(new Date(new Date().getTime()+864000000))
                .claim("accountId",userDetails.getAccountId())
                .claim("profiles",userDetails.getProfiles())
                .claim("email",authentication.getName())
                .claim("authorities", authentication.getAuthorities())
                .claim("id",userDetails.getId())
                .claim("accountType",userDetails.getAccountType())
                .signWith(key).compact();

        return jwt;
    }

    public String genarateToken(CustomUserDetails userDetails, String email, Integer profileID, List<GrantedAuthority> a, AccountType accountType){
        String jwt= Jwts.builder().setIssuer("Achien").setIssuedAt(new Date()).setExpiration(new Date(new Date().getTime()+864000000))
                .claim("accountId",userDetails.getAccountId())
                .claim("profiles",userDetails.getProfiles())
                .claim("email",email)
                .claim("authorities", a)
                .claim("id",profileID)
                .claim("accountType",accountType)
                .signWith(key).compact();

        return jwt;
    }
    public String getEmailFromToken(String jwt) {
        try {
            jwt = jwt.substring(7);
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
            return String.valueOf(claims.get("email"));
        } catch (JwtException e) {
            throw new IllegalStateException("Invalid token", e);
        }
    }

    public Integer getUserIdFromToken(String jwt) {
        try {
            if (jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return Integer.parseInt(claims.get("id").toString());
        } catch (JwtException e) {
            throw new IllegalStateException("Token không hợp lệ!", e);
        }
    }

}
