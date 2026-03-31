package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.BankAccountDTO;

public interface BankAccountService {
    String saveBankAccount(BankAccountDTO dto, String username);
    BankAccountDTO getMyBankAccount(String username);
    String updateBankAccount(BankAccountDTO dto, String username);
    String deleteMyBankAccount(String username);
}