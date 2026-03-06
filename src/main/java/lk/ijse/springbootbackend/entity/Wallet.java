package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;
    private Long interviewerId;
    private Long bankAccountId;
    private Double amount;
    private String status;

    @OneToOne
    @JoinColumn(name = "interviewerId")
    private Interviewer interviewer;

    @OneToOne
    @JoinColumn(name = "bankAccountId")
    private BankAccount defaultBankAccount;
}
