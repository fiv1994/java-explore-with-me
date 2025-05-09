package ru.practicum.explore.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

@Slf4j
@Getter
@Setter
public class UserDtoIn {
    @Email
    @NotBlank
    @Length(min = 6, max = 254)
    String email;
    @NotBlank
    @Length(min = 2, max = 250)
    String name;
}