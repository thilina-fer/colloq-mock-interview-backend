package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.PaymentDto;
import lk.ijse.springbootbackend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin("http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 🚀 දැන් අනිවාර්යයෙන්ම Postman එකෙනුත්, Frontend එකෙනුත් මේකට වැඩ කරනවා!
    @PostMapping("/checkout")
    public ResponseEntity<String> makePayment(@RequestBody PaymentDto paymentDto) {
        try {
            String result = paymentService.processPayment(paymentDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing payment: " + e.getMessage());
        }
    }
}