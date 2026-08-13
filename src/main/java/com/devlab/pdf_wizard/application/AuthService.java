package com.devlab.pdf_wizard.application;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.devlab.pdf_wizard.application.in.LoginUseCase;
import com.devlab.pdf_wizard.application.in.RegisterUserUseCase;
import com.devlab.pdf_wizard.application.in.command.LoginCommand;
import com.devlab.pdf_wizard.application.in.command.RegisterUserCommand;
import com.devlab.pdf_wizard.application.model.AccessToken;
import com.devlab.pdf_wizard.application.out.TokenProvider;
import com.devlab.pdf_wizard.application.out.UserLoadPort;
import com.devlab.pdf_wizard.application.out.UserSavePort;
import com.devlab.pdf_wizard.domain.exception.UserAlreadyExistsException;
import com.devlab.pdf_wizard.domain.exception.InvalidCredentialsException;
import com.devlab.pdf_wizard.domain.exception.UserDisabledException;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;
import com.devlab.pdf_wizard.domain.service.PasswordDomainService;

public class AuthService implements RegisterUserUseCase, LoginUseCase {

    private final UserLoadPort userLoadPort;
    private final UserSavePort userSavePort;
    private final PasswordDomainService passwordDomainService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthService(UserLoadPort userLoadPort, UserSavePort userSavePort,
            PasswordDomainService passwordDomainService,
            AuthenticationManager authenticationManager,
            TokenProvider tokenProvider) {
        this.userLoadPort = userLoadPort;
        this.userSavePort = userSavePort;
        this.passwordDomainService = passwordDomainService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public User register(RegisterUserCommand command) {
        userLoadPort.findByEmail(command.email())
                .ifPresent(user -> {
                    throw UserAlreadyExistsException.forEmail(command.email());
                });

        Password password = Password.fromPlainText(
                command.password(),
                passwordDomainService);
        User user = User.register(command.email(), password);

        return userSavePort.save(user);
    }

    @Override
    public AccessToken login(LoginCommand command) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            command.email().value(),
                            command.password()));
            User user = (User) authentication.getPrincipal();

            return tokenProvider.generate(user);
        } catch (DisabledException exception) {
            throw new UserDisabledException();
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException();
        }
    }
}
