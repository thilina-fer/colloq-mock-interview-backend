// dto/candidate/CandidateResponseDTO.java
package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponseDTO {
    private Long candidateId;
    private String username;
    private String email;
    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;
    private String joinDate;
}