package com.example.demo.security;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
 private final UserRepository users;
 public DatabaseUserDetailsService(UserRepository users){this.users=users;}
 @Override public UserDetails loadUserByUsername(String username){
  User u=users.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Пользователь не найден"));
  return org.springframework.security.core.userdetails.User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().getName().replace("ROLE_","")).build();
 }
}
