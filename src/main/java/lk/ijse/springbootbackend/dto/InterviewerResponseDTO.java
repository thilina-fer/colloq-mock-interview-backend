// dto/interviewer/InterviewerResponseDTO.java
package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InterviewerResponseDTO {
    private Long interviewerId;
    private String username;
    private String email;
    private String bio;
    private String company;
    private String designation;
    private Integer experienceYears; // 💡 'int' වෙනුවට 'Integer' (Capital I)
    private String specialization;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;
    private String joinSDate;


    private Double price;
    private String levelName;


}