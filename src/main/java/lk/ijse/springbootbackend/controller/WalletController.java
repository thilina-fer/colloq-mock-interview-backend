package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.WalletDTO;
import lk.ijse.springbootbackend.dto.WithdrawalHistoryDTO;
import lk.ijse.springbootbackend.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/my-wallet")
    public ResponseEntity<APIResponse> getMyWallet(Authentication authentication) {
        WalletDTO walletDTO = walletService.getMyWallet(authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Success", walletDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<APIResponse> withdraw(
            @RequestParam("amount") double amount,
            Authentication auth
    ) {
        String message = walletService.withdrawFunds(amount, auth.getName());
        return ResponseEntity.ok(new APIResponse(200, message, null));
    }

    @GetMapping("/withdrawal-history")
    public ResponseEntity<APIResponse> getHistory(Authentication auth) {
        List<WithdrawalHistoryDTO> history = walletService.getWithdrawalHistory(auth.getName());
        return ResponseEntity.ok(new APIResponse(200, "Success", history));
    }
}