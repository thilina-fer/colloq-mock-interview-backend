package lk.ijse.springbootbackend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lk.ijse.springbootbackend.dto.auth.AuthDTO;
import lk.ijse.springbootbackend.dto.auth.AuthMeDTO;
import lk.ijse.springbootbackend.dto.auth.AuthResponseDTO;
import lk.ijse.springbootbackend.dto.auth.GoogleAuthDTO;
import lk.ijse.springbootbackend.dto.auth.RegisterDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Role;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.service.AuthService;
import lk.ijse.springbootbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    // =========================
    // LOGIN
    // =========================

    @Override
    public AuthResponseDTO authenticate(AuthDTO authDTO) {

        Auth auth = authRepo.findByUsername(authDTO.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + authDTO.getUsername()));

        if (auth.getPassword() == null) {
            throw new BadCredentialsException("This account uses Google login");
        }

        if (!passwordEncoder.matches(authDTO.getPassword(), auth.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(auth.getUsername());

        return new AuthResponseDTO(token);
    }

    // =========================
    // REGISTER
    // =========================

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
                .status("ACTIVE")
                .emailVerified(false)
                .profileUpdated(false)
                .build();

        authRepo.save(auth);

        return "User registered successfully";
    }

    // =========================
    // GOOGLE LOGIN
    // =========================

    @Override
    public AuthResponseDTO authenticateWithGoogle(GoogleAuthDTO googleAuthDTO) {

        try {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(googleAuthDTO.getIdToken());

            if (googleIdToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String picture = (String) payload.get("picture");
            Boolean emailVerified = payload.getEmailVerified();

            Auth auth = authRepo.findByGoogleId(googleId)
                    .orElseGet(() -> authRepo.findByEmail(email).orElse(null));

            if (auth == null) {

                auth = Auth.builder()
                        .username(email)
                        .password(null)
                        .email(email)
                        .googleId(googleId)
                        .role(googleAuthDTO.getRole())
                        .emailVerified(emailVerified != null && emailVerified)
                        .profilePic(picture)
                        .status("ACTIVE")
                        .profileUpdated(false)
                        .build();

            } else {

                auth.setGoogleId(googleId);
                auth.setEmail(email);
                auth.setEmailVerified(emailVerified != null && emailVerified);
                auth.setProfilePic(picture);

                if (auth.getRole() == null && googleAuthDTO.getRole() != null) {
                    auth.setRole(googleAuthDTO.getRole());
                }

            }

            authRepo.save(auth);

            String token = jwtUtil.generateToken(auth.getUsername());

            return new AuthResponseDTO(token);

        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

    // =========================
    // CURRENT USER (/me)
    // =========================

    @Override
    public AuthMeDTO getCurrentUser(String username) {

        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        return new AuthMeDTO(
                auth.getUsername(),
                auth.getEmail(),
                auth.getRole(),
                auth.getEmailVerified(),
                auth.getProfilePic(),
                auth.getStatus(),
                auth.isProfileUpdated()
        );
    }

}