package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.dto.SignInRequest;
import com.aufsell.Lab1.dto.SignUpRequest;
import com.aufsell.Lab1.dto.UserRegistrationResponse;
import com.aufsell.Lab1.exception.PasswordAlreadyTakenException;
import com.aufsell.Lab1.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        try {
            UserRegistrationResponse response = authenticationService.signUp(signUpRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (PasswordAlreadyTakenException e) {
            return new ResponseEntity<>("THIS PASSWORD ALREADY TAKEN", HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Авторизация")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserRegistrationResponse response = authenticationService.signIn(signInRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
