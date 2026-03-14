package task.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    private final String jwtSecret;
    private final Long jwtExpirationMs;

    public JwtProvider(@Value("${jwt.secret}") String jwtSecret,
                       @Value("${jwt.expirationMs}") Long jwtExpirationMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)); // создаем ключ для подписания
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username) //кому выдан
                .claim("role", role) // кастомные данные, роль
                .issuedAt(new Date()) // дата выдачи
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // время смерти токена
                .signWith(getSigningKey()) // подписание токена ключом
                .compact(); // сборка в строку
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // проверка подписи
                    .build()
                    .parseSignedClaims(token); // расшифровка
            return true;
        } catch (Exception e) { // если токен подделан, просрочен или поврежден
            return false;
        }
    }

    public String getUsernameByToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload(); // получаем данные

        return claims.getSubject(); // возвращаем username
    }
}
