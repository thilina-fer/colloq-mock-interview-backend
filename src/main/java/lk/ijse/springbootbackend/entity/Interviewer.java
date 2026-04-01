package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter // 💡 @Data වෙනුවට මේවා පාවිච්චි කරන්න
@Setter
public class Interviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interviewerId;

    private String joinSDate;
    private String bio;
    private String company;
    private Integer experienceYears;
    private String specialization;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authId")
    @ToString.Exclude
    private Auth auth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "levelId")
    private Level level;

    @OneToOne(mappedBy = "interviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;

}