package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long withdrawalId;
//    private Long transactionId;
//    private Long walletId;
    private Double requestedAmount;
    private String processedDate;
    private String processedTime;
    private String status;

    @OneToOne
    @JoinColumn(name = "transactionId")
    private WalletTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "walletId")
    private Wallet wallet;
}
