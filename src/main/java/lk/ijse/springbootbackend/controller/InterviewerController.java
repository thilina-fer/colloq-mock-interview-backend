package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interviewer")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InterviewerController {
    private final InterviewerService interviewerService;

    @PostMapping("/complete-interviewer-profile")
    public String completeProfile(@RequestBody CompleteInterviewerProfileDTO completeInterviewerProfileDTO , Authentication authentication) {
        return interviewerService.completeInterviewerProfile(completeInterviewerProfileDTO , authentication.getName());
    }

}
