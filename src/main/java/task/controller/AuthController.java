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
import task.dto.AuthDto;
import task.model.Role;
import task.model.User;
import task.security.JwtProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> login(@RequestBody AuthDto authDto) {
        try {
            String username = authDto.getUsername();
            String password = authDto.getPassword();

            //аутентификация пользователя
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // если проверка прошла, получаем данные пользователя
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            // получаем роль
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // генерим токен
            String token = jwtProvider.generateToken(userDetails.getUsername(), roles);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response); // вернули 200 и токен

        } catch (BadCredentialsException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Неверный пароль");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDto  authDto) {
        String username = authDto.getUsername();
        String password = authDto.getPassword();

        Optional<User> user = userDao.findByUsername(username);
        if (user.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Пользователь с таким именем уже существует");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singleton(Role.ROLE_USER)); // создаем множество и кладем туда только один элемент

        userDao.update(newUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Пользователь успешно зарегистрирован");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
