package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.SystemProfitDTO;
import lk.ijse.springbootbackend.dto.WalletDTO;
import lk.ijse.springbootbackend.entity.*;
import lk.ijse.springbootbackend.repo.*;
import lk.ijse.springbootbackend.service.SystemProfitService;
import lk.ijse.springbootbackend.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepo walletRepo;
    private final InterviewerRepo interviewerRepo;
    private final SystemProfitService systemProfitService;
    private final AuthRepo authRepo;
    private  final BankAccountRepo bankAccountRepo;
    private final WithdrawalHistoryRepo withdrawalHistoryRepo;

    @Override
    @Transactional
    public void processTransaction(Long bookingId, Long interviewerId, Double totalAmount) {
        Double interviewerShare = totalAmount * 0.90;
        Double systemShare = totalAmount * 0.10;
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        Wallet wallet = walletRepo.findByInterviewer(interviewer)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setInterviewer(interviewer);
                    newWallet.setBalance(0.0);
                    newWallet.setStatus("ACTIVE");
                    return newWallet;
                });

        wallet.setBalance(wallet.getBalance() + interviewerShare);
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepo.save(wallet);
        systemProfitService.recordProfit(bookingId, systemShare);
    }

    @Override
    public WalletDTO getMyWallet(String username) {
        return walletRepo.findByInterviewerUsername(username)
                .map(wallet -> new WalletDTO(
                        wallet.getWalletId(),
                        wallet.getBalance(),
                        wallet.getStatus(),
                        wallet.getLastUpdated(),
                        wallet.getInterviewer().getAuth().getUsername()
                ))
                .orElseGet(() -> {
                    WalletDTO emptyWallet = new WalletDTO();
                    emptyWallet.setBalance(0.0);
                    emptyWallet.setStatus("ACTIVE");
                    emptyWallet.setInterviewerName(username);
                    return emptyWallet;
                });
    }

    @Override
    @Transactional
    public String withdrawFunds(Double amount, String username) {
        System.out.println("🚀 [DEBUG] Withdrawal Request - Amount: " + amount + " | User: " + username);

        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        BankAccount bankAccount = bankAccountRepo.findByInterviewer(interviewer)
                .orElseThrow(() -> new RuntimeException("Please link a bank account before withdrawing funds."));

        Wallet wallet = walletRepo.findByInterviewer(interviewer)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in your wallet.");
        }

        if (amount < 1000) {
            throw new RuntimeException("Minimum withdrawal amount is LKR 1,000.00");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepo.save(wallet);

        WithdrawalHistory history = new WithdrawalHistory();
        history.setAmount(amount);
        history.setWithdrawalDate(LocalDateTime.now());
        history.setInterviewer(interviewer);
        history.setBankDetails(bankAccount.getBankName() + " - " + bankAccount.getAccountNumber());
        withdrawalHistoryRepo.save(history);

        return "Withdrawal of LKR " + amount + " was successful and processed to your bank account.";
    }

}