package task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.dao.UserDao;
import task.dto.AuthRequestDto;
import task.dto.AuthResponseDto;
import task.dto.MessageResponseDto;
import task.model.Role;
import task.model.User;
import task.security.JwtProvider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider,  UserDao userDao,  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto authRequestDto) {
        try {
            String username = authRequestDto.getUsername();
            String password = authRequestDto.getPassword();

            //аутентификация пользователя
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // если проверка прошла, получаем данные пользователя
            if (!(auth.getPrincipal() instanceof UserDetails userDetails)) {
                throw new BadCredentialsException("Не удалось получить данные пользователя");
            }

            // получаем роль
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // генерим токен
            String token = jwtProvider.generateToken(userDetails.getUsername(), roles);

            return ResponseEntity.ok(new AuthResponseDto(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponseDto("Неверное имя пользователя или пароль"));
        }
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(@RequestBody AuthRequestDto authRequestDto) {
        String username = authRequestDto.getUsername();
        String password = authRequestDto.getPassword();

        Optional<User> user = userDao.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDto("Пользователь с таким именем уже существует"));
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singleton(Role.ROLE_USER)); // создаем множество и кладем туда только один элемент

        userDao.update(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDto("Пользователь успешно зарегистрирован"));
    }
}
