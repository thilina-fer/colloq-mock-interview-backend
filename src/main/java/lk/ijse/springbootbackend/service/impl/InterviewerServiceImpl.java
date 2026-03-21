package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewerServiceImpl implements InterviewerService {
    private final AuthRepo authRepo;
    private final InterviewerRepo interviewerRepo;

    @Override
    public String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        if (interviewerRepo.existsByAuth(auth)){
            throw new RuntimeException("Interviewer already exists");
        }
        Interviewer interviewer = new Interviewer();
        interviewer.setAuth(auth);
        interviewer.setJoinSDate(java.time.LocalDate.now().toString());
        interviewer.setBio(dto.getBio());
        interviewer.setCompany(dto.getCompany());
        interviewer.setDesignation(dto.getDesignation());
        interviewer.setExperienceYears(dto.getExperienceYears());
        interviewer.setSpecialization(dto.getSpecialization());
        interviewer.setGithubUrl(dto.getGithubUrl());
        interviewer.setLinkedinUrl(dto.getLinkedinUrl());
        interviewer.setProfilePicture(dto.getProfilePicture());
//        interviewer.setStatus(dto.getStatus());
        interviewer.setStatus("ACTIVE");
        interviewerRepo.save(interviewer);
        return "Interviewer profile completed successfully";

    }

    @Override
    public InterviewerResponseDTO getInterviewerProfile(String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        return mapToDTO(interviewer);
    }

    @Override
    public String updateInterviewerProfile(CompleteInterviewerProfileDTO dto, String username) {
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        interviewer.setBio(dto.getBio());
        interviewer.setCompany(dto.getCompany());
        interviewer.setDesignation(dto.getDesignation());
        interviewer.setExperienceYears(dto.getExperienceYears());
        interviewer.setSpecialization(dto.getSpecialization());
        interviewer.setGithubUrl(dto.getGithubUrl());
        interviewer.setLinkedinUrl(dto.getLinkedinUrl());
        interviewer.setProfilePicture(dto.getProfilePicture());
        if (dto.getStatus() != null) interviewer.setStatus(dto.getStatus());

        interviewerRepo.save(interviewer);
        return "Interviewer profile updated successfully";
    }

    @Override
    public String deleteInterviewerProfile(Long interviewerId, String username) {
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        interviewerRepo.delete(interviewer);
        return "Interviewer profile deleted successfully";
    }

    @Override
    public List<InterviewerResponseDTO> getAllInterviewers() {
        return interviewerRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper method
    private InterviewerResponseDTO mapToDTO(Interviewer interviewer) {
        return new InterviewerResponseDTO(
                interviewer.getInterviewerId(),
                interviewer.getAuth().getUsername(),
                interviewer.getAuth().getEmail(),
                interviewer.getBio(),
                interviewer.getCompany(),
                interviewer.getDesignation(),
                interviewer.getExperienceYears(),
                interviewer.getSpecialization(),
                interviewer.getGithubUrl(),
                interviewer.getLinkedinUrl(),
                interviewer.getProfilePicture(),
                interviewer.getStatus(),
                interviewer.getJoinSDate()
        );
    }
}
