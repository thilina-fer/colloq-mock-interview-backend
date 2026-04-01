package lk.ijse.springbootbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.entity.Level;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.repo.LevelRepo;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewerServiceImpl implements InterviewerService {

    private final InterviewerRepo interviewerRepo;
    private final AuthRepo authRepo;
    private final LevelRepo levelRepo; // 🎯 Inject LevelRepo
    private final Cloudinary cloudinary;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, MultipartFile imageFile, String username) {
        Auth auth = authRepo.findByUsername(username)
                .or(() -> authRepo.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer record not found. Please register first."));

        interviewer.setBio(dto.getBio());
        interviewer.setCompany(dto.getCompany());
        interviewer.setExperienceYears(dto.getExperienceYears()); // Integer Mapping
        interviewer.setGithubUrl(dto.getGithubUrl());
        interviewer.setLinkedinUrl(dto.getLinkedinUrl());

        interviewer.setStatus("PENDING");

        if (dto.getSpecializations() != null && !dto.getSpecializations().isEmpty()) {
            interviewer.setSpecialization(String.join(", ", dto.getSpecializations()));
        }

        if (dto.getLevelId() != null) {
            Level level = levelRepo.findById(dto.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Level not found with ID: " + dto.getLevelId()));
            interviewer.setLevel(level);
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        com.cloudinary.utils.ObjectUtils.asMap("folder", "colloq_profiles/interviewers"));
                String imageUrl = uploadResult.get("url").toString();
                interviewer.setProfilePicture(imageUrl);
                auth.setProfilePic(imageUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }

        interviewerRepo.save(interviewer);
        auth.setProfileUpdated(true);
        authRepo.save(auth);

        return "Interviewer profile submitted successfully and pending admin approval";
    }
    @Override
    @Transactional
    public String updateInterviewerProfile(CompleteInterviewerProfileDTO dto, MultipartFile imageFile, String username) {
        Auth auth = authRepo.findByUsername(username)
                .or(() -> authRepo.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        interviewer.setBio(dto.getBio());
        interviewer.setCompany(dto.getCompany());
        interviewer.setExperienceYears(dto.getExperienceYears());
        interviewer.setGithubUrl(dto.getGithubUrl());
        interviewer.setLinkedinUrl(dto.getLinkedinUrl());

        if (dto.getSpecializations() != null && !dto.getSpecializations().isEmpty()) {
            interviewer.setSpecialization(String.join(", ", dto.getSpecializations()));
        }

        if (dto.getLevelId() != null) {
            Level level = levelRepo.findById(dto.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Level not found"));
            interviewer.setLevel(level);
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "colloq_profiles/interviewers"));

                String imageUrl = uploadResult.get("url").toString();
                interviewer.setProfilePicture(imageUrl);
                auth.setProfilePic(imageUrl);
            } else if (dto.getProfilePicture() != null) {
                interviewer.setProfilePicture(dto.getProfilePicture());
                auth.setProfilePic(dto.getProfilePicture());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile picture: " + e.getMessage());
        }

        interviewerRepo.save(interviewer);
        authRepo.save(auth);

        return "Interviewer profile updated successfully";
    }



    //  Candidate Selection Modal
    @Override
    public List<InterviewerResponseDTO> getActiveInterviewers() {
        return interviewerRepo.findAll().stream()
                .filter(i -> "ACTIVE".equalsIgnoreCase(i.getStatus()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🎯 Admin Dashboard එකට (Pending සහ Active හැමෝම)
    @Override
    public List<InterviewerResponseDTO> getAllInterviewersForAdmin() {
        return interviewerRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private InterviewerResponseDTO mapToDTO(Interviewer interviewer) {
        InterviewerResponseDTO dto = new InterviewerResponseDTO();
        dto.setInterviewerId(interviewer.getInterviewerId());
        dto.setBio(interviewer.getBio());
        dto.setCompany(interviewer.getCompany());
        dto.setExperienceYears(interviewer.getExperienceYears());
        dto.setSpecialization(interviewer.getSpecialization());
        dto.setStatus(interviewer.getStatus());
        dto.setProfilePicture(interviewer.getProfilePicture());
        if (interviewer.getWallet() != null) {
            dto.setWalletBalance(interviewer.getWallet().getBalance());
        } else {
            dto.setWalletBalance(0.0);
        }

        // Auth details
        if (interviewer.getAuth() != null) {
            dto.setUsername(interviewer.getAuth().getUsername());
            dto.setEmail(interviewer.getAuth().getEmail());
        }

        // Level details
        if (interviewer.getLevel() != null) {
            dto.setLevelName(interviewer.getLevel().getName());
            dto.setPrice(interviewer.getLevel().getPrice());
            dto.setLevelId(interviewer.getLevel().getLevelId());
        }

        return dto;
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
    public String deleteInterviewerProfile(Long interviewerId, String username) {
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));
        interviewerRepo.delete(interviewer);
        return "Interviewer profile deleted successfully";
    }

    @Override
    public List<InterviewerResponseDTO> getPendingInterviewers() {
        return interviewerRepo.findAll().stream()
                .filter(i -> i.getStatus() != null && i.getStatus().trim().equalsIgnoreCase("PENDING"))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InterviewerResponseDTO approveInterviewer(Long interviewerId) {
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer not found with ID: " + interviewerId));

        interviewer.setStatus("ACTIVE");

        if (interviewer.getAuth() != null) {
            interviewer.getAuth().setStatus("ACTIVE");
            authRepo.save(interviewer.getAuth());
        }

        Interviewer savedInterviewer = interviewerRepo.save(interviewer);
        return mapToDTO(savedInterviewer);
    }

    @Override
    @Transactional
    public String rejectInterviewer(Long interviewerId) {
        if (!interviewerRepo.existsById(interviewerId)) {
            throw new RuntimeException("Interviewer not found");
        }
        interviewerRepo.deleteById(interviewerId);
        return "Interviewer application rejected and removed successfully";
    }
}