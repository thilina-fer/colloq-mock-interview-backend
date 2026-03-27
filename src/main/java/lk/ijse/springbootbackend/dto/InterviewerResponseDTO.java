// dto/interviewer/InterviewerResponseDTO.java
package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewerResponseDTO {
    private Long interviewerId;
    private String username;
    private String email;
    private String bio;
    private String company;
    private String designation;
    private Integer experienceYears;
    private String specialization;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;
    private String joinSDate;


    public InterviewerResponseDTO(Long interviewerId, String username, String email, String bio, String company, String designation, Integer experienceYears, String specialization, String githubUrl, String linkedinUrl, String profilePicture, String status) {
    }
}