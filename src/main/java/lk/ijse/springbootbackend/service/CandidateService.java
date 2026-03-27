package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.dto.CandidateResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CandidateService {
    // imageFile eka add kala
    String completeCandidateProfile(CompleteCandidateProfileDTO dto, MultipartFile imageFile, String username);
    CandidateResponseDTO getCandidateProfile(String username);

    // CandidateService.java
    CandidateResponseDTO updateCandidateProfile(CandidateResponseDTO dto, MultipartFile imageFile, String username);

    String deleteCandidateProfile(Long candidateId, String username);
    List<CandidateResponseDTO> getAllCandidates();
}