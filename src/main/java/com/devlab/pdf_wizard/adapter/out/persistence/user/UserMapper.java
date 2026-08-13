package com.devlab.pdf_wizard.adapter.out.persistence.user;

import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail().value(),
                user.getPassword().value(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public static User toDomain(UserEntity entity) {
        return User.restore(
                entity.getId(),
                Email.of(entity.getEmail()),
                Password.fromHashed(entity.getPasswordHash()),
                entity.getRole(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
