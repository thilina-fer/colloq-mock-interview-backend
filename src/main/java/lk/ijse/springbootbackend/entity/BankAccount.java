package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankAccountId;
    private Long interviewerId;
    private String bankName;
    private String accountNumber;
    private Boolean isDefault;
    private String status;

    @ManyToOne
    @JoinColumn(name = "interviewerId")
    private Interviewer interviewer;
}
