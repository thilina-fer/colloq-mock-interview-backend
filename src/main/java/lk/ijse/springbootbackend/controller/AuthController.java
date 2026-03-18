package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.auth.*;
import lk.ijse.springbootbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
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

    @PostMapping("/google")
    public AuthResponseDTO googleLogin(@RequestBody GoogleAuthDTO googleAuthDTO) {
        return authService.authenticateWithGoogle(googleAuthDTO);
    }

    @GetMapping("/me")
    public AuthMeDTO me(Authentication authentication){
        return authService.getCurrentUser(authentication.getName());
    }
}