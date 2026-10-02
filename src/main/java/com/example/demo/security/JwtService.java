package com.example.demo.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {
    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long expiration;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public JwtService(ObjectMapper mapper,
                      @Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration}") long expiration) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT_SECRET должен содержать не менее 32 байт");
        }
        this.mapper = mapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expiration = expiration;
    }

    public String generate(Authentication authentication) {
        try {
            long now = Instant.now().getEpochSecond();
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", authentication.getName());
            payload.put("iat", now);
            payload.put("exp", now + expiration);
            payload.put("roles", authentication.getAuthorities().stream().map(Object::toString).toList());
            String content = encodeJson(header) + "." + encodeJson(payload);
            return content + "." + encoder.encodeToString(sign(content));
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать JWT", e);
        }
    }

    public Map<String, Object> verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Некорректный JWT");
            String content = parts[0] + "." + parts[1];
            if (!java.security.MessageDigest.isEqual(sign(content), decoder.decode(parts[2]))) {
                throw new IllegalArgumentException("Некорректная подпись JWT");
            }
            Map<String, Object> claims = mapper.readValue(decoder.decode(parts[1]), new TypeReference<>() {});
            Number exp = (Number) claims.get("exp");
            if (exp == null || exp.longValue() <= Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("Срок действия JWT истёк");
            }
            return claims;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный JWT", e);
        }
    }

    public long getExpiration() { return expiration; }

    private String encodeJson(Object value) throws Exception {
        return encoder.encodeToString(mapper.writeValueAsBytes(value));
    }

    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }
}
