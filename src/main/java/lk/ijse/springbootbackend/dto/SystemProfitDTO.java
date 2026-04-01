package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemProfitDTO {
    private Long profitId;
    private Double amount;
    private LocalDateTime receivedDate;
    private Long bookingId;
}