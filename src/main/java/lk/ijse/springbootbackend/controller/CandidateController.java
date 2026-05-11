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

    @PostMapping(value = "/complete-profile", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> completeProfile(
            @RequestPart("data") CompleteCandidateProfileDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {

        String result = candidateService.completeCandidateProfile(dto, imageFile, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Success", result));
    }
    @PostMapping(value = "/update-profile", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> updateProfile(
            @RequestPart("data") CandidateResponseDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {
        CandidateResponseDTO updatedData = candidateService.updateCandidateProfile(dto, imageFile, authentication.getName());

        return ResponseEntity.ok(new APIResponse(200, "Candidate profile updated successfully", updatedData));
    }

    @DeleteMapping("/delete-profile/{candidateId}")
    public ResponseEntity<APIResponse> deleteProfile(@PathVariable Long candidateId,
                                                     Authentication authentication) {
        String msg = candidateService.deleteCandidateProfile(candidateId, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, msg, null));
    }

    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAllCandidates() {
        List<CandidateResponseDTO> data = candidateService.getAllCandidates();
        return ResponseEntity.ok(new APIResponse(200, "Success", data));
    }
}