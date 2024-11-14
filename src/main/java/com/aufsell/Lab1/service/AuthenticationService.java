package com.aufsell.Lab1.service;

import com.aufsell.Lab1.dto.JwtAuthenticationResponse;
import com.aufsell.Lab1.dto.SignInRequest;
import com.aufsell.Lab1.dto.SignUpRequest;
import com.aufsell.Lab1.exception.PasswordAlreadyTakenException;
import com.aufsell.Lab1.model.Role;
import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (isPasswordTaken(passwordEncoder.encode(request.getPassword()))) {
            throw new PasswordAlreadyTakenException("THIS PASSWORD ALREADY TAKEN");
        }
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    private boolean isPasswordTaken(String password) {
        // Проверка пароля в базе данных (проверка по хешу пароля или прямому сравнению)
        return userRepository.existsByPassword(password);  // Это пример, лучше использовать хеши паролей
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
