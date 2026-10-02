package com.example.demo.config;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class DataInitializer {
 @Bean CommandLineRunner initialData(RoleRepository roles,UserRepository users,PasswordEncoder encoder){return args->{
  for(String n:new String[]{"ROLE_USER","ROLE_PHARMACIST","ROLE_ADMIN"}) if(roles.findByName(n).isEmpty()){Role r=new Role();r.setName(n);roles.save(r);}
  if(users.count()==0){User a=new User();a.setUsername("admin");a.setEmail("admin@pharmacy.local");a.setFullName("Администратор");a.setPassword(encoder.encode("Admin123!"));a.setRole(roles.findByName("ROLE_ADMIN").orElseThrow());users.save(a);}
 };}
}
