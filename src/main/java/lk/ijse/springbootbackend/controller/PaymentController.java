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
@CrossOrigin("http://localhost:5173") // 🎯 Frontend URL එකට අවසර දීම
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<String> makePayment(@RequestBody PaymentDto paymentDto, Principal principal) {
        try {
            // 🎯 Principal එකෙන් ලොග් වුණු User ගේ නම ගන්න පුළුවන් (Logged in Candidate)
            // දැනට අපි කෙලින්ම service එකට paymentDto එක යවනවා
            String result = paymentService.processPayment(paymentDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // මොකක් හරි වැරදුණොත් Error message එකක් යවනවා
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}