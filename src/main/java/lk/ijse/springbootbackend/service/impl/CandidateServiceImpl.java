package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Candidate;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.CandidateRepo;
import lk.ijse.springbootbackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}