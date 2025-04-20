package com.radutodosan.mechanics.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailRequestDTO {
    private String newEmail;
    private String password;
}
