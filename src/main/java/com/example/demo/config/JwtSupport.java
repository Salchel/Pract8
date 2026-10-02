package com.example.demo.config;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.*;
public final class JwtSupport {
 private JwtSupport(){}
 public static Converter<Jwt,? extends AbstractAuthenticationToken> converter(){
  JwtGrantedAuthoritiesConverter g=new JwtGrantedAuthoritiesConverter(); g.setAuthoritiesClaimName("roles");g.setAuthorityPrefix("");
  JwtAuthenticationConverter c=new JwtAuthenticationConverter();c.setJwtGrantedAuthoritiesConverter(g);return c;
 }
}
