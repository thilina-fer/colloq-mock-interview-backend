/*
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
*/


package lk.ijse.springbootbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthMeDTO {
    // === Common Fields (Dennatama podu ewa) ===
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
    // Candidate log weddi mewa nikanma null widihata pass wei (Eka normal)
    private String company;
    private String designation;
    private Integer experienceYears;
    private String specialization;
}