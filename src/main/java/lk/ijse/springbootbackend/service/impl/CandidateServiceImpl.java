package lk.ijse.springbootbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lk.ijse.springbootbackend.dto.auth.CompleteCandidateProfileDTO;
import lk.ijse.springbootbackend.dto.CandidateResponseDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Candidate;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.CandidateRepo;
import lk.ijse.springbootbackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepo candidateRepo;
    private final AuthRepo authRepo;
    private final Cloudinary cloudinary; // Cloudinary eka inject kala

    @Override
    public String completeCandidateProfile(CompleteCandidateProfileDTO dto, MultipartFile imageFile, String username) {

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
        candidate.setStatus("ACTIVE");

        // Cloudinary Image Upload Logic
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "colloq_profiles"));
                candidate.setProfilePicture(uploadResult.get("url").toString());
            } else {
                candidate.setProfilePicture(dto.getProfilePicture()); // default ui-avatar eka
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }

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
    public CandidateResponseDTO updateCandidateProfile(CandidateResponseDTO dto, MultipartFile imageFile, String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Candidate candidate = candidateRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Candidate profile not found"));

        // Username update logic
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            if (!auth.getUsername().equals(dto.getUsername())) {
                if (authRepo.existsByUsername(dto.getUsername())) {
                    throw new RuntimeException("Username already taken!");
                }
                auth.setUsername(dto.getUsername());
                authRepo.save(auth);
            }
        }

        candidate.setBio(dto.getBio());
        candidate.setGithubUrl(dto.getGithubUrl());
        candidate.setLinkedinUrl(dto.getLinkedinUrl());

        // Image Upload
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "colloq_profiles"));
                candidate.setProfilePicture(uploadResult.get("url").toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image");
        }

        candidateRepo.save(candidate);

        // Meka thamai wadagath: Aluth data tika DTO ekakata harawala return karanna
        return mapToDTO(candidate);
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