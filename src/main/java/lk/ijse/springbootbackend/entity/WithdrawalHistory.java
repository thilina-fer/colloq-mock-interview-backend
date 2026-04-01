package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private LocalDateTime withdrawalDate;
    private String bankDetails;

    @ManyToOne
    @JoinColumn(name = "interviewerId")
    private Interviewer interviewer;
}