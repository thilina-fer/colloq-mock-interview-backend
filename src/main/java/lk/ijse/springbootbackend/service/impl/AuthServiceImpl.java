package lk.ijse.springbootbackend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lk.ijse.springbootbackend.dto.auth.*;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Candidate;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.entity.Role;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.CandidateRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.AuthService;
import lk.ijse.springbootbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepo authRepo;
    private final CandidateRepo candidateRepo;
    private final InterviewerRepo interviewerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    public AuthResponseDTO authenticate(AuthDTO authDTO) {
        // Username හෝ Email දෙකෙන් එකකින් Login වෙන්න ඉඩ දෙමු
        Auth auth = authRepo.findByUsername(authDTO.getUsername())
                .or(() -> authRepo.findByEmail(authDTO.getUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (auth.getPassword() == null) {
            throw new BadCredentialsException("This account uses Google login");
        }

        if (!passwordEncoder.matches(authDTO.getPassword(), auth.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // auth.getRole().name() පාවිච්චි කරලා Role එක String එකක් විදිහට යවමු
        String token = jwtUtil.generateToken(auth.getUsername(), auth.getRole().name());
        return new AuthResponseDTO(token, auth.getRole().name()); // Enum to String
    }

//    @Override
//    @Transactional
//    public String register(RegisterDTO registerDTO) {
//        if (authRepo.existsByUsername(registerDTO.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//        if (authRepo.existsByEmail(registerDTO.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        Auth auth = Auth.builder()
//                .username(registerDTO.getUsername())
//                .password(passwordEncoder.encode(registerDTO.getPassword()))
//                .email(registerDTO.getEmail())
//                .role(registerDTO.getRole()) // Enum භාවිතා වේ
//                .status("ACTIVE")
//                .emailVerified(false)
//                .profileUpdated(false)
//                .build();
//
//        authRepo.save(auth);
//        return "User registered successfully";
//    }

    @Override
    @Transactional
    public String register(RegisterDTO registerDTO) {
        // 1. මුලින්ම Auth record එක හදනවා
        Auth auth = Auth.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .email(registerDTO.getEmail())
                .role(registerDTO.getRole())
                .status("ACTIVE")
                .emailVerified(false)
                .profileUpdated(false)
                .build();

        Auth savedAuth = authRepo.save(auth);

        // 2. Role එක CANDIDATE නම්, Candidate table එකට default values දානවා
        if (savedAuth.getRole() == Role.CANDIDATE) {
            Candidate candidate = new Candidate();
            candidate.setAuth(savedAuth);

            // 💡 RegisterDTO එකේ මේ දත්ත නැති නිසා අපි Default values දානවා
            candidate.setBio("Professional Candidate at ColloQ");
            candidate.setGithubUrl("https://github.com/placeholder");
            candidate.setLinkedinUrl("https://linkedin.com/in/placeholder");
            candidate.setJoinDate(String.valueOf(LocalDate.now()));
            candidate.setStatus("ACTIVE");

            candidateRepo.save(candidate);
        }

        return "User registered successfully";
    }
    @Override
    @Transactional
    public AuthResponseDTO authenticateWithGoogle(GoogleAuthDTO googleAuthDTO) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(googleAuthDTO.getIdToken());
            if (googleIdToken == null) throw new RuntimeException("Invalid Google ID token");

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();

            Auth auth = authRepo.findByGoogleId(googleId)
                    .orElseGet(() -> authRepo.findByEmail(email).orElse(null));

            if (auth == null) {
                auth = Auth.builder()
                        .username(email)
                        .email(email)
                        .googleId(googleId)
                        .role(Role.valueOf(googleAuthDTO.getRole())) // Enum භාවිතා වේ
                        .emailVerified(true)
                        .status("ACTIVE")
                        .profileUpdated(false)
                        .build();
            } else {
                auth.setGoogleId(googleId);
                if (auth.getRole() == null) auth.setRole(Role.valueOf(googleAuthDTO.getRole()));
            }

            authRepo.save(auth);
            // auth.getRole().name() පාවිච්චි කරලා Role එක String එකක් විදිහට යවමු
            String token = jwtUtil.generateToken(auth.getUsername(), auth.getRole().name());
            return new AuthResponseDTO(token, auth.getRole().name());

        } catch (Exception e) {
            throw new RuntimeException("Google auth failed: " + e.getMessage());
        }
    }

    @Override
    public AuthMeDTO getCurrentUser(String username) {
        Auth auth = authRepo.findByUsername(username)
                .or(() -> authRepo.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        AuthMeDTO dto = new AuthMeDTO();
        dto.setAuthId(auth.getAuthId());
        dto.setUsername(auth.getUsername());
        dto.setEmail(auth.getEmail());
        dto.setRole(auth.getRole().name());

        // 💡 මුලින්ම Auth එකේ තියෙන status එක දාමු (Candidates ලාට මේක වැඩ කරයි)
        dto.setStatus(auth.getStatus());

        dto.setProfileUpdated(auth.isProfileUpdated());
        dto.setProfilePic(auth.getProfilePic());

        if (Role.CANDIDATE.equals(auth.getRole())) {
            candidateRepo.findByAuth(auth).ifPresent(c -> {
                dto.setBio(c.getBio());
                dto.setGithubUrl(c.getGithubUrl());
                dto.setLinkedinUrl(c.getLinkedinUrl());
            });
        } else if (Role.INTERVIEWER.equals(auth.getRole())) {
            interviewerRepo.findByAuth(auth).ifPresent(i -> {
                // ✅ වැදගත්ම දේ: Interviewer කෙනෙක් නම්, Auth status එක වෙනුවට
                // Interviewer table එකේ තියෙන ඇත්තම status එක (PENDING/ACTIVE) DTO එකට දාන්න.
                dto.setStatus(i.getStatus());

                dto.setBio(i.getBio());
                dto.setSpecialization(i.getSpecialization());
                dto.setCompany(i.getCompany());
                dto.setDesignation(i.getDesignation());
                dto.setExperienceYears(i.getExperienceYears());
                dto.setGithubUrl(i.getGithubUrl());
                dto.setLinkedinUrl(i.getLinkedinUrl());
            });
        }
        return dto;
    }

    @Override
    @Transactional
    public String createAdmin(RegisterDTO registerDTO) {
        // 1. Email එක දැනටමත් තියෙනවද බලන්න
        if (authRepo.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. අලුත් Admin කෙනෙක් විදිහට save කරන්න
        Auth admin = Auth.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(Role.ADMIN) // කෙලින්ම ADMIN role එක දෙනවා
                .status("ACTIVE")
                .emailVerified(true)
                .profileUpdated(true)
                .build();

        authRepo.save(admin);
        return "New Admin added successfully by " + registerDTO.getUsername();
    }
}