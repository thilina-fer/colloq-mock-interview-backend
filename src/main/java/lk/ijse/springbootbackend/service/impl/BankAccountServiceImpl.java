package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.BankAccountDTO;
import lk.ijse.springbootbackend.entity.BankAccount;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.repo.BankAccountRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepo bankAccountRepo;
    private final InterviewerRepo interviewerRepo;
    private final ModelMapper modelMapper;

    @Override
    public String saveBankAccount(BankAccountDTO dto) {
        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        BankAccount bankAccount = modelMapper.map(dto, BankAccount.class);
        bankAccount.setInterviewer(interviewer);
        bankAccountRepo.save(bankAccount);
        return "Bank account saved successfully";
    }

    @Override
    public List<BankAccountDTO> getAccountsByInterviewer(Long interviewerId) {
        return bankAccountRepo.findByInterviewer_InterviewerId(interviewerId).stream()
                .map(acc -> modelMapper.map(acc, BankAccountDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String updateBankAccount(Long id, BankAccountDTO dto) {
        BankAccount existing = bankAccountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        existing.setBankName(dto.getBankName());
        existing.setAccountNumber(dto.getAccountNumber());
        existing.setIsDefault(dto.getIsDefault());
        existing.setStatus(dto.getStatus());

        bankAccountRepo.save(existing);
        return "Bank account updated successfully";
    }

    @Override
    public String deleteBankAccount(Long id) {
        if(!bankAccountRepo.existsById(id)) throw new RuntimeException("Account not found");
        bankAccountRepo.deleteById(id);
        return "Bank account deleted successfully";
    }
}