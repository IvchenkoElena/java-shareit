package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class UpdateUserRequest {
    private String name;
    @Email
    private String email;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }
}
