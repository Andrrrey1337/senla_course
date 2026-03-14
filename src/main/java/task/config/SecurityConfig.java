package task.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import task.security.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // чтобы сверять зашифрованный пароль из БД с тем, что ввел пользователь
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // отключаем csrf так как нет сессий
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)) // Без сессий
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/bookings/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/api/data/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/guests/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/guests/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/rooms/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/services/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/services/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/service-records/**").hasAuthority("ROLE_ADMIN")
                                .anyRequest().authenticated())
                .exceptionHandling(exc -> exc
                        // обработка ошибки 401 Unauthorized (Пользователь не авторизован)
                        .authenticationEntryPoint((request, response, authException) -> {
                            //  формат ответа
                            response.setContentType("application/json;charset=UTF-8");
                            // HTTP статус 401
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"необходима авторизация (токен отсутствует или недействителен)\"}");
                        })

                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            // HTTP статус 403
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"У вас нет прав администратора для выполнения этой операции\"}");
                        })
                )
                // подключаем наш фильтр
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
