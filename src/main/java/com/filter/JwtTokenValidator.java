package com.filter;

import com.constant.AccountType;
import com.constant.JwtConstant;
import com.service.imple.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {
    //private static final List<String> EXCLUDED_PATHS = Arrays.asList("/motelRoom/allMotelRoom","/redis/set","/redis/get","/redis/delete");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt=request.getHeader("Authorization");
        String path = request.getRequestURI();
        System.out.println(path+" ne");
        if (!path.startsWith("/api/google/login/user")
                &&!path.startsWith("/api/auth/signup")
                &&!path.startsWith("/api/auth/signin")
                &&!path.startsWith("/api/auth/authAccount")
                &&!path.startsWith("/api/videos/")
                &&!path.startsWith("/api/public")
                &&!path.startsWith("/api/favicon.ico")
                &&!path.startsWith("/api/sse/")
                &&!path.startsWith("/api/ws")
                &&!path.startsWith("/api/sockjs")){

            System.out.println("Du dieu kien");
            if(jwt!=null && jwt.startsWith("Bearer ")){
                try {
                    //bearer tokens
                    jwt=jwt.substring(7);
                    SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                    Claims claims= Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                    String username=String.valueOf(claims.get("email"));
                    Integer id = claims.get("id", Integer.class);
                    String authorities=String.valueOf(claims.get("authorities"));
                    Integer accountId= claims.get("accountId", Integer.class);
                    AccountType accountType=AccountType.valueOf(String.valueOf(claims.get("accountType")));

                    CustomUserDetails userDetails=new CustomUserDetails(accountId,id,username);

                    userDetails.setAccountType(accountType);


                    List<GrantedAuthority> auths= AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                    Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,null,auths);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
                catch (Exception e){
                    throw new RuntimeException("Invalid token");
                }

            }
            else{
                throw new RuntimeException("Invalid token");
            }
        }
        filterChain.doFilter(request,response);
    }
}
