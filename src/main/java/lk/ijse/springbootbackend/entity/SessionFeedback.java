package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SessionFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;
//    private Long bookingId;
    private Integer rating;
    private String feedbackComment;
    private String status;

    @OneToOne
    @JoinColumn(name = "bookingId")
    private Bookings bookings;
}
