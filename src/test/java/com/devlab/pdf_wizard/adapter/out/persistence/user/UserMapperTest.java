package com.devlab.pdf_wizard.adapter.out.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.devlab.pdf_wizard.domain.model.Email;
import com.devlab.pdf_wizard.domain.model.Password;
import com.devlab.pdf_wizard.domain.model.User;

class UserMapperTest {

    @Test
    void shouldMapDomainToEntityAndBackWithoutLosingData() {
        User user = User.register(
                Email.of("user@example.com"),
                Password.fromHashed("stored-password-hash"));

        UserEntity entity = UserMapper.toEntity(user);
        User restoredUser = UserMapper.toDomain(entity);

        assertThat(entity.getPasswordHash()).isEqualTo("stored-password-hash");
        assertThat(restoredUser.getId()).isEqualTo(user.getId());
        assertThat(restoredUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(restoredUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(restoredUser.getRole()).isEqualTo(user.getRole());
        assertThat(restoredUser.isEnabled()).isEqualTo(user.isEnabled());
        assertThat(restoredUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(restoredUser.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
    }
}
