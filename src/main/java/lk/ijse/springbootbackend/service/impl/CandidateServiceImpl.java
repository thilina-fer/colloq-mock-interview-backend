package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.dto.candidate.CandidateResponseDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Candidate;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.CandidateRepo;
import lk.ijse.springbootbackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepo candidateRepo;
    private final AuthRepo authRepo;

    @Override
    public String completeCandidateProfile(CompleteCandidateProfileDTO dto, String username) {

        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        if (candidateRepo.existsByAuth(auth)) {
            throw new RuntimeException("Candidate profile already exists");
        }

        Candidate candidate = new Candidate();
        candidate.setAuth(auth);
        candidate.setJoinDate(java.time.LocalDate.now().toString());
        candidate.setBio(dto.getBio());
        candidate.setGithubUrl(dto.getGithubUrl());
        candidate.setLinkedinUrl(dto.getLinkedinUrl());
        candidate.setProfilePicture(dto.getProfilePicture());
        candidate.setStatus("ACTIVE");

        candidateRepo.save(candidate);

        return "Candidate profile completed successfully";
    }

    @Override
    public CandidateResponseDTO getCandidateProfile(String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Candidate candidate = candidateRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        return mapToDTO(candidate);
    }

    @Override
    public String updateCandidateProfile(CompleteCandidateProfileDTO dto, String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Candidate candidate = candidateRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        candidate.setBio(dto.getBio());
        candidate.setGithubUrl(dto.getGithubUrl());
        candidate.setLinkedinUrl(dto.getLinkedinUrl());
        candidate.setProfilePicture(dto.getProfilePicture());
        if (dto.getStatus() != null) candidate.setStatus(dto.getStatus());

        candidateRepo.save(candidate);
        return "Candidate profile updated successfully";
    }

    @Override
    public String deleteCandidateProfile(Long candidateId, String username) {
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        candidateRepo.delete(candidate);
        return "Candidate profile deleted successfully";
    }

    @Override
    public List<CandidateResponseDTO> getAllCandidates() {
        return candidateRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper method
    private CandidateResponseDTO mapToDTO(Candidate candidate) {
        return new CandidateResponseDTO(
                candidate.getCandidateId(),
                candidate.getAuth().getUsername(),
                candidate.getAuth().getEmail(),
                candidate.getBio(),
                candidate.getGithubUrl(),
                candidate.getLinkedinUrl(),
                candidate.getProfilePicture(),
                candidate.getStatus(),
                candidate.getJoinDate()
        );
    }

}