package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import lk.ijse.springbootbackend.dto.auth.RegisterDTO;
import lk.ijse.springbootbackend.service.AuthService;
import lk.ijse.springbootbackend.service.InterviewerService;
import lk.ijse.springbootbackend.service.SystemProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
//@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final InterviewerService interviewerService;
    private final SystemProfitService systemProfitService;

    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(authService.createAdmin(registerDTO));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok("Only Admins can see this");
    }


//    @GetMapping("/pending-interviewers")
//    public ResponseEntity<APIResponse> getPending() {
//        System.out.println("🚀 [CRITICAL DEBUG] AdminController hit successful!");
//        return ResponseEntity.ok(new APIResponse(200, "Success", interviewerService.getPendingInterviewers()));
//    }

    @GetMapping("/pending-interviewers")
    public ResponseEntity<APIResponse> getPending() {
//        System.out.println("[DEBUG] AdminController: getPending method HITTED!");

        try {
            List data = interviewerService.getPendingInterviewers();
           // System.out.println("[DEBUG] Data fetched size: " + (data != null ? data.size() : "NULL"));
            return ResponseEntity.ok(new APIResponse(200, "Success", data));
        } catch (Exception e) {
//            System.out.println("[DEBUG] Error in Service: " + e.getMessage());
            throw e;
        }
    }


    @PutMapping("/approve-interviewer/{id}")
    public ResponseEntity<APIResponse> approveInterviewer(@PathVariable("id") Long interviewerId) {
        InterviewerResponseDTO approvedDto = interviewerService.approveInterviewer(interviewerId);

        return new ResponseEntity<>(
                new APIResponse(200, "Interviewer Approved Successfully", approvedDto),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/reject-interviewer/{id}")
    public ResponseEntity<APIResponse> rejectInterviewer(@PathVariable("id") Long interviewerId) {
        String message = interviewerService.rejectInterviewer(interviewerId);
        return new ResponseEntity<>(
                new APIResponse(200, message, null),
                HttpStatus.OK
        );
    }

    @GetMapping("/total-profit")
    public ResponseEntity<APIResponse> getTotalProfit() {
        Double totalProfit = systemProfitService.getTotalProfit();
        Double result = (totalProfit != null) ? totalProfit : 0.0;
        return ResponseEntity.ok(new APIResponse(200, "Success", result));
    }

    @GetMapping("/profit-history")
    public ResponseEntity<APIResponse> getProfitHistory() {
        return ResponseEntity.ok(new APIResponse(200, "Success", systemProfitService.getAllProfits()));
    }
}