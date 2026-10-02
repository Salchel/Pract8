package com.example.demo.controller.api;
import com.example.demo.model.*;import com.example.demo.repository.*;import com.example.demo.service.*;import io.swagger.v3.oas.annotations.*;import io.swagger.v3.oas.annotations.security.SecurityRequirement;import io.swagger.v3.oas.annotations.tags.Tag;import jakarta.validation.Valid;import java.util.*;import org.springframework.data.domain.Sort;import org.springframework.http.*;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api") @PreAuthorize("hasRole('ADMIN')") @SecurityRequirement(name="bearerAuth") @Tag(name="Администрирование",description="Пользователи, профили и роли") public class AdministrationApiController {private final UserService users;private final UserRepository userRepo;private final UserProfileService profiles;private final RoleRepository roles;public AdministrationApiController(UserService u,UserRepository ur,UserProfileService p,RoleRepository r){users=u;userRepo=ur;profiles=p;roles=r;}
 @GetMapping("/users")@Operation(summary="Список пользователей без паролей")List<User> users(@RequestParam(required=false)String q){return users.search(q);}
 @GetMapping("/users/{id}")@Operation(summary="Пользователь по ID")User user(@PathVariable Long id){return users.findById(id).orElseThrow();}
 @PutMapping("/users/{id}/role")@Operation(summary="Изменить роль пользователя")User role(@PathVariable Long id,@RequestParam Long roleId){users.changeRole(id,roleId);return users.findById(id).orElseThrow();}
 @DeleteMapping("/users/{id}")@ResponseStatus(HttpStatus.NO_CONTENT)@Operation(summary="Удалить пользователя")void deleteUser(@PathVariable Long id){userRepo.deleteById(id);}
 @GetMapping("/profiles")@Operation(summary="Список профилей")List<UserProfile> profiles(@RequestParam(required=false)String q){return profiles.search(q);}
 @GetMapping("/profiles/{id}")@Operation(summary="Профиль по ID")UserProfile profile(@PathVariable Long id){return profiles.findById(id).orElseThrow();}
 @PostMapping("/profiles")@ResponseStatus(HttpStatus.CREATED)@Operation(summary="Создать профиль")UserProfile addProfile(@Valid @RequestBody UserProfile v){v.setId(null);return profiles.save(v);}
 @PutMapping("/profiles/{id}")@Operation(summary="Изменить профиль")UserProfile editProfile(@PathVariable Long id,@Valid @RequestBody UserProfile v){profiles.findById(id).orElseThrow();v.setId(id);return profiles.save(v);}
 @DeleteMapping("/profiles/{id}")@ResponseStatus(HttpStatus.NO_CONTENT)@Operation(summary="Удалить профиль")void deleteProfile(@PathVariable Long id){profiles.delete(id);}
 @GetMapping("/roles")@Operation(summary="Список ролей")List<Role> roles(){return roles.findAll(Sort.by("id"));}
 @GetMapping("/roles/{id}")@Operation(summary="Роль по ID")Role role(@PathVariable Long id){return roles.findById(id).orElseThrow();}
 @PostMapping("/roles")@ResponseStatus(HttpStatus.CREATED)@Operation(summary="Создать роль")Role addRole(@Valid @RequestBody Role v){v.setId(null);return roles.save(v);}
 @PutMapping("/roles/{id}")@Operation(summary="Изменить роль")Role editRole(@PathVariable Long id,@Valid @RequestBody Role v){roles.findById(id).orElseThrow();v.setId(id);return roles.save(v);}
 @DeleteMapping("/roles/{id}")@ResponseStatus(HttpStatus.NO_CONTENT)@Operation(summary="Удалить роль")void deleteRole(@PathVariable Long id){roles.deleteById(id);}
}
