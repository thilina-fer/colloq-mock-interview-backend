package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviewer")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class InterviewerController {
    private final InterviewerService interviewerService;

    @PostMapping("/complete-interviewer-profile")
    public String completeProfile(@RequestBody CompleteInterviewerProfileDTO completeInterviewerProfileDTO , Authentication authentication) {
        return interviewerService.completeInterviewerProfile(completeInterviewerProfileDTO , authentication.getName());
    }

    // READ - own profile
    @GetMapping("/profile")
    public ResponseEntity<APIResponse> getProfile(Authentication authentication) {
        InterviewerResponseDTO data = interviewerService.getInterviewerProfile(authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Success", data));
    }

    // UPDATE
//    @PutMapping("/update-profile")
//    public ResponseEntity<APIResponse> updateProfile(
//            @RequestBody CompleteInterviewerProfileDTO dto,
//            Authentication authentication) {
//        String msg = interviewerService.updateInterviewerProfile(dto, authentication.getName());
//        return ResponseEntity.ok(new APIResponse(200, msg, null));
//    }

    @PutMapping("/update-profile")
    public ResponseEntity<APIResponse> updateProfile(@RequestBody CompleteInterviewerProfileDTO dto, Authentication authentication) {
        String data = interviewerService.updateInterviewerProfile(dto, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Profile updated successfully", data));
    }

    // DELETE
    @DeleteMapping("/delete-profile/{interviewerId}")
    public ResponseEntity<APIResponse> deleteProfile(
            @PathVariable Long interviewerId,
            Authentication authentication) {
        String msg = interviewerService.deleteInterviewerProfile(interviewerId, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, msg, null));
    }

    // GET ALL
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAllInterviewers() {
        List<InterviewerResponseDTO> data = interviewerService.getAllInterviewers();
        return ResponseEntity.ok(new APIResponse(200, "Success", data));
    }

}
