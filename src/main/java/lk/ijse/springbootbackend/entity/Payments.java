package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Double amount;
    private String paymentMethod;
    private String transactionId;
    private String status;

    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "bookingId")
    private Bookings bookings;
}