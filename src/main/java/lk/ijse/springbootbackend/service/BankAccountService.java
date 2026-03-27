package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.BankAccountDTO;
import java.util.List;

public interface BankAccountService {
    String saveBankAccount(BankAccountDTO dto);
    List<BankAccountDTO> getAccountsByInterviewer(Long interviewerId);
    String updateBankAccount(Long id, BankAccountDTO dto);
    String deleteBankAccount(Long id);
}