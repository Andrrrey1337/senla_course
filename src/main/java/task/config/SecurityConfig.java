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
import task.model.Role;
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
                        // доступ для всех
                        .requestMatchers("/api/auth/**").permitAll()

                        // 1) строгие пути
                        .requestMatchers(HttpMethod.GET, "/api/rooms/occupied").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/rooms/occupied/count").hasAuthority(Role.ROLE_ADMIN.name())

                        // для юзера и админа
                        .requestMatchers(HttpMethod.GET, "/api/rooms").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/rooms/available").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/rooms/available/count").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/rooms/available/date").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/services").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())

                        // только для юзера
                        .requestMatchers(HttpMethod.POST, "/api/bookings/check-in").hasAuthority(Role.ROLE_USER.name())
                        .requestMatchers(HttpMethod.POST, "/api/service-records/order").hasAuthority(Role.ROLE_USER.name())

                        // 2) пути с переменными
                        .requestMatchers(HttpMethod.GET, "/api/rooms/{number}/payment").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/bookings/check-out/{roomNumber}").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/rooms/{number}").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())

                        // 3) маски ** в самый конец, чтобы они не поглотили другие запросы
                        .requestMatchers("/api/data/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers("/api/guests/**").hasAuthority(Role.ROLE_ADMIN.name())
                        // добавил защиту истории заселений и просмотра услуг
                        .requestMatchers("/api/residences/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/service-records/**").hasAuthority(Role.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.POST, "/api/rooms/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/services/**").hasAuthority(Role.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/services/**").hasAuthority(Role.ROLE_ADMIN.name())

                        // 4) все остальные запросы
                        .anyRequest().authenticated()
                )
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
