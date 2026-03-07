package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/complete-profile")
    public String completeProfile(@RequestBody CompleteCandidateProfileDTO dto,
                                  Authentication authentication) {
        return candidateService.completeCandidateProfile(dto, authentication.getName());
    }
}