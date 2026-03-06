package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    private Long candidateId;
    private Long interviewerId;
    private Long levelId;
    private String jobType;
    private String scheduleDate;
    private String scheduleTime;
    private String candidateNote;
    private String meetingLink;
    private String status;

    @ManyToOne
    @JoinColumn(name = "candidateId")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "interviewerId")
    private Interviewer interviewer;

    @ManyToOne
    @JoinColumn(name = "levelId")
    private Level level;
}
