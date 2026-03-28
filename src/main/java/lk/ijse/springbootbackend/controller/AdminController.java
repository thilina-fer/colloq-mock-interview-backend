package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.auth.RegisterDTO;
import lk.ijse.springbootbackend.service.AuthService;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final InterviewerService interviewerService;

    // අලුත් Admin කෙනෙක්ව add කිරීම
    @PostMapping("/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody RegisterDTO registerDTO) {
        // මෙතනදී AuthService එකේ අපි අලුතින් හදපු method එක call කරනවා
        return ResponseEntity.ok(authService.createAdmin(registerDTO));
    }

    // පද්ධතියේ ඉන්න ඔක්කොම Users ලව බලාගන්න (Admin ට විතරයි පුළුවන්)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        // මේක පස්සේ හදමු
        return ResponseEntity.ok("Only Admins can see this");
    }


    @GetMapping("/pending-interviewers")
    public ResponseEntity<APIResponse> getPending() {
        return ResponseEntity.ok(new APIResponse(200, "Success", interviewerService.getPendingInterviewers()));
    }

    // AdminController.java

    @PutMapping("/approve-interviewer/{id}")
    public ResponseEntity<APIResponse> approve(@PathVariable("interviewer id") Long id) { // 💡 මෙතන ("id") අනිවාර්යයි
        String result = interviewerService.approveInterviewer(id);
        return ResponseEntity.ok(new APIResponse(200, "Success", result));
    }
}