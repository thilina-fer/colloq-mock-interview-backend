package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    private String status;

    @OneToOne
    @JoinColumn(name = "interviewer_id", nullable = false, unique = true)
    @ToString.Exclude
    private Interviewer interviewer;
}