package com.filter;

import com.DTO.ProfileDTO;
import com.constant.AccountType;
import com.constant.JwtConstant;
import com.service.CustomUserDetails;
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
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {
    //private static final List<String> EXCLUDED_PATHS = Arrays.asList("/motelRoom/allMotelRoom","/redis/set","/redis/get","/redis/delete");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt=request.getHeader("Authorization");
        String path = request.getRequestURI();
        if (!path.startsWith("/google/login/user")
                &&!path.startsWith("/auth/signup")
                &&!path.startsWith("/auth/signin")
                &&!path.startsWith("/auth/authAccount")
                &&!path.startsWith("/videos/")
                &&!path.startsWith("/sse/")
                &&!path.startsWith("/sockjs")){
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
                    List<?> rawProfiles = claims.get("profiles", List.class);
                    AccountType accountType=AccountType.valueOf(String.valueOf(claims.get("accountType")));
                    List<ProfileDTO> profiles = new java.util.ArrayList<>();

                    for (Object obj : rawProfiles) {
                        if (obj instanceof LinkedHashMap<?, ?> map) {
                            ProfileDTO dto = new ProfileDTO();
                            dto.setId((Integer) map.get("id"));
                            dto.setName((String) map.get("name"));
                            dto.setAvt((String) map.get("avt"));
                            dto.setAccountType(AccountType.valueOf((String)map.get("accountType")));
                            profiles.add(dto);
                        }
                    }
                    CustomUserDetails userDetails=new CustomUserDetails(accountId,id,username);

                    userDetails.setProfiles(profiles);
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
