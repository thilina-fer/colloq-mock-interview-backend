package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.auth.AuthDTO;
import lk.ijse.springbootbackend.dto.auth.AuthResponseDTO;
import lk.ijse.springbootbackend.dto.auth.RegisterDTO;
import lk.ijse.springbootbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthDTO authDTO) {
        return authService.authenticate(authDTO);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }
}