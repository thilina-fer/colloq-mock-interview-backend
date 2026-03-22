package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountDTO {
    private Long bankAccountId;
    private String bankName;
    private String accountNumber;
    private Boolean isDefault;
    private String status;
    private Long interviewerId;
}