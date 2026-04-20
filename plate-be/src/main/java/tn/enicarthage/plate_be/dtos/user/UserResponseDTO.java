package tn.enicarthage.plate_be.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String role;
    private String avatarUrl;
}

