package lk.ijse.springbootbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompleteCandidateProfileDTO {
    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;
}
