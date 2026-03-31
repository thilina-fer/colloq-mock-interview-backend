package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.BankAccountDTO;
import lk.ijse.springbootbackend.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bank-account")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/save-account")
    public ResponseEntity<String> linkAccount(@RequestBody BankAccountDTO dto, Authentication authentication) {
        String response = bankAccountService.saveBankAccount(dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-account")
    public ResponseEntity<BankAccountDTO> getMyAccount(Authentication authentication) {
        return ResponseEntity.ok(bankAccountService.getMyBankAccount(authentication.getName()));
    }

    @PutMapping("/update-account")
    public ResponseEntity<String> updateAccount(@RequestBody BankAccountDTO dto, Authentication authentication) {
        String response = bankAccountService.updateBankAccount(dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-account")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        String response = bankAccountService.deleteMyBankAccount(authentication.getName());
        return ResponseEntity.ok(response);
    }
}