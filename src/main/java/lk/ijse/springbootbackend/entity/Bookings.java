package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private String jobType;

    @Column(columnDefinition = "TEXT")
    private String candidateNote;

    private String meetingLink;

    private String googleCalendarEventId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 50)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidateId", nullable = false)
    private Candidate candidate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availabilityId", nullable = false)
    private InterviewerAvailability availability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewerId", nullable = false)
    private Interviewer interviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelId", nullable = false)
    private Level level;
}