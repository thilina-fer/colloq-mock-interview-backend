package lk.ijse.springbootbackend.dto.auth;

import lk.ijse.springbootbackend.entity.Role;
import lombok.Data;

@Data
public class RegisterDTO {
    private  String username;
    private String password;
    private String email;
    private Role role;
}
