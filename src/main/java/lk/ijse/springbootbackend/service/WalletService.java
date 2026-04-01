package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.WalletDTO;
import lk.ijse.springbootbackend.dto.SystemProfitDTO;
import java.util.List;

public interface WalletService {
    void processTransaction(Long bookingId, Long interviewerId, Double totalAmount);
    WalletDTO getMyWallet(String username);
    String withdrawFunds(Double amount, String username);
}