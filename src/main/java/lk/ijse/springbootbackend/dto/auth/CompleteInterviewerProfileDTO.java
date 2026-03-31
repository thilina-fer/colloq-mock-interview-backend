//package lk.ijse.springbootbackend.dto.auth;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//public class CompleteInterviewerProfileDTO {
//    private String bio;
//    private String company;
//    private String designation;
//    private Integer experienceYears;
//    private String specialization;
//    private String githubUrl;
//    private String linkedinUrl;
//    private String profilePicture;
//    private String status;
//}


package lk.ijse.springbootbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List; // Specializations ලිස්ට් එකක් විදිහට ගන්න

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompleteInterviewerProfileDTO {
    private String bio;
    private String company;
    private Long levelId;
    private Integer experienceYears;
    private List<String> specializations;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;
}