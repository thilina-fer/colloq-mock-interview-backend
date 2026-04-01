package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalHistoryDTO {
    private Long id;
    private Double amount;
    private LocalDateTime withdrawalDate;
    private String bankDetails;
}