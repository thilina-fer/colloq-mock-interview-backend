package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
