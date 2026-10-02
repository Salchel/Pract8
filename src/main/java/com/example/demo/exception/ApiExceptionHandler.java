package com.example.demo.exception;
import java.time.Instant;import java.util.*;import org.springframework.dao.DataIntegrityViolationException;import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice(basePackages="com.example.demo.controller.api") public class ApiExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class)ResponseEntity<?> validation(MethodArgumentNotValidException e){Map<String,String> fields=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->fields.put(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(Map.of("timestamp",Instant.now(),"status",400,"message","Ошибка валидации","fields",fields));}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class,DataIntegrityViolationException.class})ResponseEntity<?> bad(Exception e){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("timestamp",Instant.now(),"status",409,"message",e.getMessage()==null?"Конфликт данных":e.getMessage()));}
 @ExceptionHandler(NoSuchElementException.class)ResponseEntity<?> missing(Exception e){return ResponseEntity.status(404).body(Map.of("timestamp",Instant.now(),"status",404,"message","Запись не найдена"));}
}
