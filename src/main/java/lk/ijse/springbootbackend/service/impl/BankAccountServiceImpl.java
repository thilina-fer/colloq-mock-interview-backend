package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.BankAccountDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.BankAccount;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.BankAccountRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepo bankAccountRepo;
    private final AuthRepo authRepo;
    private final InterviewerRepo interviewerRepo;
    private final ModelMapper modelMapper;

    @Override
    public String saveBankAccount(BankAccountDTO dto, String username) {
        Interviewer interviewer = getInterviewerByUsername(username);
        if (bankAccountRepo.existsByInterviewer(interviewer)) {
            throw new RuntimeException("You already have a linked bank account. Please update the existing one.");
        }

        BankAccount bankAccount = modelMapper.map(dto, BankAccount.class);
        bankAccount.setInterviewer(interviewer);
        bankAccount.setStatus("ACTIVE");

        bankAccountRepo.save(bankAccount);
        return "Bank account linked successfully";
    }

    @Override
    public BankAccountDTO getMyBankAccount(String username) {
        Interviewer interviewer = getInterviewerByUsername(username);
        BankAccount bankAccount = bankAccountRepo.findByInterviewer(interviewer)
                .orElseThrow(() -> new RuntimeException("No bank account linked for this user"));

        return modelMapper.map(bankAccount, BankAccountDTO.class);
    }

    @Override
    public String updateBankAccount(BankAccountDTO dto, String username) {
        Interviewer interviewer = getInterviewerByUsername(username);
        BankAccount existing = bankAccountRepo.findByInterviewer(interviewer)
                .orElseThrow(() -> new RuntimeException("No bank account found to update"));

        // දත්ත update කිරීම
        existing.setAccountName(dto.getAccountName());
        existing.setBankName(dto.getBankName());
        existing.setBranchName(dto.getBranchName());
        existing.setAccountNumber(dto.getAccountNumber());

        bankAccountRepo.save(existing);
        return "Bank account updated successfully";
    }

    @Override
    public String deleteMyBankAccount(String username) {
        Interviewer interviewer = getInterviewerByUsername(username);
        BankAccount bankAccount = bankAccountRepo.findByInterviewer(interviewer)
                .orElseThrow(() -> new RuntimeException("No bank account found to delete"));

        bankAccountRepo.delete(bankAccount);
        return "Bank account removed successfully";
    }

    // Common helper method to find interviewer using username from token
    private Interviewer getInterviewerByUsername(String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));
        return interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));
    }
}