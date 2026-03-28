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
    private String designation;
    private Integer experienceYears;
    private String specialization;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePicture;
    private String status;

    @OneToOne(fetch = FetchType.EAGER) // 💡 Eager load කරන්න ෂුවර් වෙන්න
    @JoinColumn(name = "authId")
    @ToString.Exclude // 💡 මේක අනිවාර්යයි (Infinite loop එක නවත්වන්න)
    private Auth auth;
}