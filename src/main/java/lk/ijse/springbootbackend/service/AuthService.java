package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.AuthDTO;
import lk.ijse.springbootbackend.dto.auth.AuthMeDTO;
import lk.ijse.springbootbackend.dto.auth.AuthResponseDTO;
import lk.ijse.springbootbackend.dto.auth.GoogleAuthDTO;
import lk.ijse.springbootbackend.dto.auth.RegisterDTO;

public interface AuthService {

    AuthResponseDTO authenticate(AuthDTO authDTO);

    String register(RegisterDTO registerDTO);

    AuthResponseDTO authenticateWithGoogle(GoogleAuthDTO googleAuthDTO);

    AuthMeDTO getCurrentUser(String username);
    String createAdmin(RegisterDTO registerDTO);

}