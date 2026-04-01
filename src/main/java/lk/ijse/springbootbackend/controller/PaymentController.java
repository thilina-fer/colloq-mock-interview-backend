package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.PaymentDto;
import lk.ijse.springbootbackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<String> makePayment(@RequestBody PaymentDto paymentDto, Principal principal) {
        try {
            String result = paymentService.processPayment(paymentDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}