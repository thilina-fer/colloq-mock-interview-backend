package lk.ijse.springbootbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthMeDTO {
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;
    private String profilePic;
    private String status;
    private boolean profileUpdated;
}
