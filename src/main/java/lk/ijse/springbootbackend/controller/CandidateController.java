    package lk.ijse.springbootbackend.controller;

    import lk.ijse.springbootbackend.dto.APIResponse;
    import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
    import lk.ijse.springbootbackend.dto.candidate.CandidateResponseDTO;
    import lk.ijse.springbootbackend.entity.Candidate;
    import lk.ijse.springbootbackend.service.CandidateService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/v1/candidate")
    @RequiredArgsConstructor
    @CrossOrigin("*")
    public class CandidateController {

        private final CandidateService candidateService;

        @PostMapping("/complete-profile")
        public String completeProfile(@RequestBody CompleteCandidateProfileDTO dto,
                                      Authentication authentication) {
            return candidateService.completeCandidateProfile(dto, authentication.getName());
        }

        // UPDATE
        @PutMapping("/update-profile")
        public ResponseEntity<APIResponse> updateProfile(
                @RequestBody CompleteCandidateProfileDTO dto,
                Authentication authentication) {
            String msg = candidateService.updateCandidateProfile(dto, authentication.getName());
            return ResponseEntity.ok(new APIResponse(200, msg, null));
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