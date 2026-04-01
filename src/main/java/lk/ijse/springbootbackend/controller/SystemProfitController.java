package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.service.SystemProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/profits")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class SystemProfitController {

    private final SystemProfitService systemProfitService;

    @GetMapping("/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> getTotalSystemProfit() {
        Double total = systemProfitService.getTotalProfit();
        Double result = (total != null) ? total : 0.0;
        return ResponseEntity.ok(new APIResponse(200, "Success", result));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse> getAllProfits() {
        return ResponseEntity.ok(new APIResponse(200, "Success", systemProfitService.getAllProfits()));
    }
}