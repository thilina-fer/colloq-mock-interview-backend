package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateId;
    private Long authId;
    private String joinDate;
    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;

    @OneToOne
    @JoinColumn(name = "authId")
    private Auth auth;
}
