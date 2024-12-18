package com.aufsell.Lab1.service;

import com.aufsell.Lab1.dto.SignInRequest;
import com.aufsell.Lab1.dto.SignUpRequest;
import com.aufsell.Lab1.dto.UserRegistrationResponse;
import com.aufsell.Lab1.exception.PasswordAlreadyTakenException;
import com.aufsell.Lab1.model.Role;
import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
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
     * @return сообщение об успешной регистрации
     */
    public UserRegistrationResponse signUp(SignUpRequest request) {
        if (isPasswordTaken(passwordEncoder.encode(request.getPassword()))) {
            throw new PasswordAlreadyTakenException("THIS PASSWORD ALREADY TAKEN");
        }
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);
        return new UserRegistrationResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

    private boolean isPasswordTaken(String password) {
        // Проверка пароля в базе данных (проверка по хешу пароля или прямому сравнению)
        return userRepository.existsByPassword(password);  // Это пример, лучше использовать хеши паролей
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return сообщение об успешной аутентификации
     */
    public UserRegistrationResponse signIn(SignInRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        var user = userService
                .getByUsername(request.getUsername());

        return new UserRegistrationResponse(user.getId(), user.getUsername(), user.getRole().name());
    }
}
