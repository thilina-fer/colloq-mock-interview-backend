package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.dto.CandidateResponseDTO;
import lk.ijse.springbootbackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class CandidateController {

    private final CandidateService candidateService;

    // COMPLETE PROFILE - Multipart form data support add kala
    @PostMapping(value = "/complete-profile", consumes = {"multipart/form-data"})
    public String completeProfile(
            @RequestPart("data") CompleteCandidateProfileDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {
        return candidateService.completeCandidateProfile(dto, imageFile, authentication.getName());
    }

    @PostMapping(value = "/update-profile", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> updateProfile(
            @RequestPart("data") CandidateResponseDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {

        // 1. Service eken ena updated object eka ganna
        CandidateResponseDTO updatedData = candidateService.updateCandidateProfile(dto, imageFile, authentication.getName());

        // 2. Eka APIResponse eke 3weni parameter eka (data field eka) widiyata yawanna
        return ResponseEntity.ok(new APIResponse(200, "Candidate profile updated successfully", updatedData));
    }
    // DELETE
    @DeleteMapping("/delete-profile/{candidateId}")
    public ResponseEntity<APIResponse> deleteProfile(@PathVariable Long candidateId,
                                                     Authentication authentication) {
        String msg = candidateService.deleteCandidateProfile(candidateId, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, msg, null));
    }

    // GET ALL
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAllCandidates() {
        List<CandidateResponseDTO> data = candidateService.getAllCandidates();
        return ResponseEntity.ok(new APIResponse(200, "Success", data));
    }
}