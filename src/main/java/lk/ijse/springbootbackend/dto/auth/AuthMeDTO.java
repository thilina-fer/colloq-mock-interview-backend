package lk.ijse.springbootbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthMeDTO {
    private Long authId;

    // === Common Fields ===
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;
    private String profilePic;
    private String status;
    private boolean profileUpdated;

    private String bio;
    private String githubUrl;
    private String linkedinUrl;

    // === Interviewer Specific Fields ===
    private String company;
    private Integer experienceYears;
    private String specialization;

    // 🎯 [NEW] Level details (Dashboard එකේ පේන්න ඕනෙ නිසා)
    private String levelName;
    private Double price;
}