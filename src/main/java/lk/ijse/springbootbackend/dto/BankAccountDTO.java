package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountDTO {
    private String accountName;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String status;
}