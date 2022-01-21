package com.jinuk.dto;

import com.jinuk.domain.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public User toEntity() {

        return User.builder()
            .email(email)
            .password(password)
            .build();
    }
}
