package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.dto.CandidateResponseDTO;

import java.util.List;

public interface CandidateService {
    String completeCandidateProfile(CompleteCandidateProfileDTO dto, String username);
    CandidateResponseDTO getCandidateProfile(String username);
    String updateCandidateProfile(CompleteCandidateProfileDTO dto, String username);
    String deleteCandidateProfile(Long candidateId, String username);
    List<CandidateResponseDTO> getAllCandidates();
}
