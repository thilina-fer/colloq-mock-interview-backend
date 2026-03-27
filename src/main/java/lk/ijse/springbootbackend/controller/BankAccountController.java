package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.BankAccountDTO;
import lk.ijse.springbootbackend.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bank-account")
@RequiredArgsConstructor
@CrossOrigin
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody BankAccountDTO dto) {
        return ResponseEntity.ok(bankAccountService.saveBankAccount(dto));
    }

    @GetMapping("/interviewer/{id}")
    public ResponseEntity<List<BankAccountDTO>> getByInterviewer(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountsByInterviewer(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody BankAccountDTO dto) {
        return ResponseEntity.ok(bankAccountService.updateBankAccount(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.deleteBankAccount(id));
    }
}