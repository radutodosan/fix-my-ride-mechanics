package com.radutodosan.mechanics.dtos;

import com.radutodosan.mechanics.entities.Mechanic;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Mechanic mechanic;
}
