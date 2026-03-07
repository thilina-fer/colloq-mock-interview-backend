package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.auth.AuthDTO;
import lk.ijse.springbootbackend.dto.auth.AuthResponseDTO;
import lk.ijse.springbootbackend.dto.auth.RegisterDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Role;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.service.AuthService;
import lk.ijse.springbootbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO authenticate(AuthDTO authDTO) {

        Auth auth = authRepo.findByUsername(authDTO.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + authDTO.getUsername()));

        if (!passwordEncoder.matches(authDTO.getPassword(), auth.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(auth.getUsername());

        return new AuthResponseDTO(token);
    }

    @Override
    public String register(RegisterDTO registerDTO) {

        if (authRepo.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Auth auth = Auth.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .email(registerDTO.getEmail())
                .role(String.valueOf(Role.valueOf(registerDTO.getRole())))
                .build();

        authRepo.save(auth);

        return "User registered successfully";
    }
}