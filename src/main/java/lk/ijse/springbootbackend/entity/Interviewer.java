package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewerId;
//    private Long authId;
    private String joinSDate;
    private String bio;
    private String company;
    private String designation;
    private Integer experienceYears;
    private String specialization;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;

    @OneToOne
    @JoinColumn(name = "authId")
    private Auth auth;
}
