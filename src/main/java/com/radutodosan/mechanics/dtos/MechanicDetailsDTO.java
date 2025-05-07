package com.radutodosan.mechanics.dtos;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MechanicDetailsDTO {
    private String username;
    private String email;
    private String pictureUrl;
}
