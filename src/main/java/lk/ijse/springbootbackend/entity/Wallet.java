package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    // private Long interviewerId;
    // private Long bankAccountId;

    private Double amount;
    private String status;

    @OneToOne
    @JoinColumn(name = "interviewerId") // Oyaage parana nama ehemama thiyenawa
    private Interviewer interviewer;

    @OneToOne
    @JoinColumn(name = "bankAccountId") // Oyaage parana nama ehemama thiyenawa
    private BankAccount defaultBankAccount;
}