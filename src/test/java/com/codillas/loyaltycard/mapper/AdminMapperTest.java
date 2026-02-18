package com.codillas.loyaltycard.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.codillas.loyaltycard.controller.dto.AdminDto;
import com.codillas.loyaltycard.repository.entity.AdminEntity;
import com.codillas.loyaltycard.repository.entity.Status;
import com.codillas.loyaltycard.repository.entity.Type;
import com.codillas.loyaltycard.service.model.Admin;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdminMapperTest {

    private final AdminMapper adminMapper = new AdminMapper();

    @Test
    void toDto_shouldMapAllFields() {
        // given
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        Admin admin = new Admin();
        admin.setId(id);
        admin.setName("Admin");
        admin.setPhoneNumber("1234567890");
        admin.setEmail("admin@test.com");
        admin.setStatus(com.codillas.loyaltycard.service.model.Status.ACTIVE);
        admin.setType(com.codillas.loyaltycard.service.model.Type.ADMIN);
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        // when
        AdminDto dto = adminMapper.toDto(admin);

        // then
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Admin");
        assertThat(dto.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(dto.getEmail()).isEqualTo("admin@test.com");
        assertThat(dto.getStatus()).isEqualTo(com.codillas.loyaltycard.controller.dto.Status.ACTIVE);
        assertThat(dto.getType()).isEqualTo(com.codillas.loyaltycard.controller.dto.Type.ADMIN);
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDomain_shouldMapEntityToAdmin() {
        // given
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        AdminEntity entity = new AdminEntity();
        entity.setId(id);
        entity.setName("SuperAdmin");
        entity.setPhoneNumber("5555555555");
        entity.setEmail("super@test.com");
        entity.setPassword("encoded");
        entity.setStatus(Status.ACTIVE);
        entity.setType(Type.SUPER_ADMIN);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // when
        Admin admin = adminMapper.toDomain(entity);

        // then
        assertThat(admin.getId()).isEqualTo(id);
        assertThat(admin.getName()).isEqualTo("SuperAdmin");
        assertThat(admin.getPhoneNumber()).isEqualTo("5555555555");
        assertThat(admin.getEmail()).isEqualTo("super@test.com");
        assertThat(admin.getPassword()).isEqualTo("encoded");
        assertThat(admin.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
        assertThat(admin.getType()).isEqualTo(com.codillas.loyaltycard.service.model.Type.SUPER_ADMIN);
        assertThat(admin.getCreatedAt()).isEqualTo(now);
        assertThat(admin.getUpdatedAt()).isEqualTo(now);
    }


    @Test
    void toDomain_shouldMapDtoToAdmin() {
        // given
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        AdminDto dto = new AdminDto();
        dto.setId(id);
        dto.setName("Admin");
        dto.setPhoneNumber("1234567890");
        dto.setEmail("admin@test.com");
        dto.setStatus(com.codillas.loyaltycard.controller.dto.Status.ACTIVE);
        dto.setType(com.codillas.loyaltycard.controller.dto.Type.SUPER_ADMIN);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        // when
        Admin admin = adminMapper.toDomain(dto);

        // then
        assertThat(admin.getId()).isEqualTo(id);
        assertThat(admin.getName()).isEqualTo("Admin");
        assertThat(admin.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(admin.getEmail()).isEqualTo("admin@test.com");
        assertThat(admin.getStatus()).isEqualTo(com.codillas.loyaltycard.service.model.Status.ACTIVE);
        assertThat(admin.getType()).isEqualTo(com.codillas.loyaltycard.service.model.Type.SUPER_ADMIN);
        assertThat(admin.getCreatedAt()).isEqualTo(now);
        assertThat(admin.getUpdatedAt()).isEqualTo(now);
    }
}
