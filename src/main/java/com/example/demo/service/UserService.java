package com.example.demo.service;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class UserService {
 private final UserRepository users; private final RoleRepository roles; private final UserProfileRepository profiles; private final PasswordEncoder encoder;
 public UserService(UserRepository users,RoleRepository roles,UserProfileRepository profiles,PasswordEncoder encoder){this.users=users;this.roles=roles;this.profiles=profiles;this.encoder=encoder;}
 public List<User> findAll(){return users.findAll(Sort.by("username"));}
 public Optional<User> findById(Long id){return users.findById(id);}
 public Optional<User> findByUsername(String value){return users.findByUsername(value);}
 public List<User> search(String q){if(q==null||q.isBlank())return findAll();String x=q.toLowerCase();return findAll().stream().filter(u->u.getUsername().toLowerCase().contains(x)||u.getEmail().toLowerCase().contains(x)||u.getFullName().toLowerCase().contains(x)||String.valueOf(u.getId()).equals(x)).toList();}
 @Transactional public User register(RegisterRequest r){
  if(!r.password().equals(r.passwordConfirmation()))throw new IllegalArgumentException("Пароли не совпадают");
  if(users.existsByUsername(r.username()))throw new IllegalArgumentException("Логин уже занят");
  if(users.existsByEmail(r.email()))throw new IllegalArgumentException("Email уже используется");
  Role role=roles.findByName("ROLE_USER").orElseThrow(()->new IllegalStateException("Роль USER не настроена"));
  User u=new User();u.setUsername(r.username().trim());u.setEmail(r.email().trim());u.setFullName(r.fullName().trim());u.setPassword(encoder.encode(r.password()));u.setRole(role);u=users.save(u);
  UserProfile p=new UserProfile();p.setUser(u);p.setAddress(r.address());p.setPhone(r.phone());p.setBonusPoints(0);profiles.save(p);return u;
 }
 @Transactional public void changeRole(Long id,Long roleId){User u=users.findById(id).orElseThrow();Role r=roles.findById(roleId).orElseThrow();u.setRole(r);users.save(u);}
}
