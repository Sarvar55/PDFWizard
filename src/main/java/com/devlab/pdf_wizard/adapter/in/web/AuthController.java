package com.devlab.pdf_wizard.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devlab.pdf_wizard.adapter.in.web.model.RegisterUserRequest;
import com.devlab.pdf_wizard.adapter.in.web.model.LoginRequest;
import com.devlab.pdf_wizard.adapter.in.web.model.TokenResponse;
import com.devlab.pdf_wizard.adapter.in.web.model.UserResponse;
import com.devlab.pdf_wizard.application.in.LoginUseCase;
import com.devlab.pdf_wizard.application.in.RegisterUserUseCase;
import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.domain.model.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
            LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request) {
        User user = registerUserUseCase.register(request.toCommand());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AccessToken token = loginUseCase.login(request.toCommand());

        return ResponseEntity.ok(TokenResponse.from(token));
    }
}
