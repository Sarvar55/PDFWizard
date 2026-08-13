package com.devlab.pdf_wizard.adapter.out.persistence.user;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.devlab.pdf_wizard.application.out.UserLoadPort;
import com.devlab.pdf_wizard.application.out.UserSavePort;
import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.User;
import com.devlab.pdf_wizard.domain.exception.UserAlreadyExistsException;

@Component
public class UserPersistenceAdapter implements UserLoadPort, UserSavePort {

    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value())
                .map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        try {
            UserEntity savedEntity = repository.save(UserMapper.toEntity(user));
            return UserMapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            throw UserAlreadyExistsException.forEmail(user.getEmail());
        }
    }
}
