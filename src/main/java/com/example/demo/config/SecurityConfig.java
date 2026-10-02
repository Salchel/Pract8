package com.example.demo.config;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.example.demo.security.DatabaseUserDetailsService;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Value("${app.jwt.secret}") private String secret;
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
 private SecretKey key(){return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256");}
 @Bean JwtDecoder jwtDecoder(){return NimbusJwtDecoder.withSecretKey(key()).build();}
 @Bean JwtEncoder jwtEncoder(){return new NimbusJwtEncoder(new ImmutableSecret<>(key()));}
 @Bean @Order(1) SecurityFilterChain api(HttpSecurity http)throws Exception{
  http.securityMatcher("/api/**").csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**").permitAll().requestMatchers("/api/products/**","/api/categories/**","/api/manufacturers/**").permitAll().anyRequest().authenticated())
   .oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(JwtSupport.converter())));
  return http.build();
 }
 @Bean @Order(2) SecurityFilterChain web(HttpSecurity http)throws Exception{
  http.authorizeHttpRequests(a->a
   .requestMatchers("/","/login","/register","/access-denied","/css/**","/images/**","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
   .requestMatchers("/admin/**","/roles/**","/profiles/**").hasRole("ADMIN")
   .requestMatchers(HttpMethod.POST,"/products/**").hasAnyRole("ADMIN","PHARMACIST")
   .requestMatchers("/manufacturers/**","/categories/**","/order-items/**").hasAnyRole("ADMIN","PHARMACIST")
   .requestMatchers("/orders/**").hasAnyRole("ADMIN","PHARMACIST")
   .requestMatchers("/products/new","/products/*/edit","/products/*/delete").hasAnyRole("ADMIN","PHARMACIST")
   .requestMatchers("/cart/**","/my-orders/**","/profile/**").authenticated().anyRequest().permitAll())
   .formLogin(f->f.loginPage("/login").defaultSuccessUrl("/",true).permitAll())
   .logout(l->l.logoutSuccessUrl("/").permitAll()).exceptionHandling(e->e.accessDeniedPage("/access-denied"));
  return http.build();
 }
}
