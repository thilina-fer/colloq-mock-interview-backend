package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;

public interface CandidateService {
    String completeCandidateProfile(CompleteCandidateProfileDTO dto, String username);
}
