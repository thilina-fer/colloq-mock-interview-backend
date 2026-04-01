package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private Long walletId;
    private Double balance;
    private String status;
    private LocalDateTime lastUpdated;
    private String interviewerName;
}