//package lk.ijse.springbootbackend.service.impl;
//
//import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
//import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
//import lk.ijse.springbootbackend.entity.Auth;
//import lk.ijse.springbootbackend.entity.Interviewer;
//import lk.ijse.springbootbackend.repo.AuthRepo;
//import lk.ijse.springbootbackend.repo.InterviewerRepo;
//import lk.ijse.springbootbackend.service.InterviewerService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class InterviewerServiceImpl implements InterviewerService {
//    private final AuthRepo authRepo;
//    private final InterviewerRepo interviewerRepo;
//
//   /* @Override
//    public String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, String username) {
//        Auth auth = authRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Auth user not found"));
//
//        if (interviewerRepo.existsByAuth(auth)){
//            throw new RuntimeException("Interviewer already exists");
//        }
//        Interviewer interviewer = new Interviewer();
//        interviewer.setAuth(auth);
//        interviewer.setJoinSDate(java.time.LocalDate.now().toString());
//        interviewer.setBio(dto.getBio());
//        interviewer.setCompany(dto.getCompany());
//        interviewer.setDesignation(dto.getDesignation());
//        interviewer.setExperienceYears(dto.getExperienceYears());
//        interviewer.setSpecialization(dto.getSpecialization());
//        interviewer.setGithubUrl(dto.getGithubUrl());
//        interviewer.setLinkedinUrl(dto.getLinkedinUrl());
//        interviewer.setProfilePicture(dto.getProfilePicture());
////        interviewer.setStatus(dto.getStatus());
//        interviewer.setStatus("ACTIVE");
//        interviewerRepo.save(interviewer);
//        return "Interviewer profile completed successfully";
//
//    }*/
//
//    @Override
//    public String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, MultipartFile image, String username) {
//        Auth auth = authRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 2. අලුත් Interviewer Entity එකක් හදන්න
//        Interviewer interviewer = new Interviewer();
//        interviewer.setAuth(auth);
//        interviewer.setBio(dto.getBio());
//        interviewer.setCompany(dto.getCompany());
//        interviewer.setDesignation(dto.getDesignation());
//        interviewer.setExperienceYears(dto.getExperienceYears());
//        interviewer.setSpecialization(dto.getSpecialization());
//        interviewer.setGithubUrl(dto.getGithubUrl());
//        interviewer.setLinkedinUrl(dto.getLinkedinUrl());
//        interviewer.setStatus("PENDING");
//
//        // 3. Image එක තිබේ නම් එය handle කරන්න
//        if (image != null && !image.isEmpty()) {
//            // මෙතනදී ඔයා image එක server එකේ හරි, Cloud (S3/Cloudinary) එකක
//            හරි save කරලා
//            // ඒකේ URL එක set කරන්න ඕනේ.
//            // උදාහරණයක් ලෙස: String imageUrl = fileService.upload(image);
//            // interviewer.setProfilePicture(imageUrl);
//
//            // දැනට පරීක්ෂා කිරීමට පමණක්:
//            interviewer.setProfilePicture("uploads/" + image.getOriginalFilename());
//        } else {
//            interviewer.setProfilePicture(dto.getProfilePicture());
//        }
//
//        interviewerRepo.save(interviewer);
//        return "Interviewer profile completed successfully";
//
//    }
//
//    @Override
//    public InterviewerResponseDTO getInterviewerProfile(String username) {
//        Auth auth = authRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Auth user not found"));
//
//        Interviewer interviewer = interviewerRepo.findByAuth(auth)
//                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));
//
//        return mapToDTO(interviewer);
//    }
//
//    @Override
//    public String updateInterviewerProfile(CompleteInterviewerProfileDTO dto, String username) {
//        Auth auth = authRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Auth user not found"));
//
//        Interviewer interviewer = interviewerRepo.findByAuth(auth)
//                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));
//
//        interviewer.setBio(dto.getBio());
//        interviewer.setCompany(dto.getCompany());
//        interviewer.setDesignation(dto.getDesignation());
//        interviewer.setExperienceYears(dto.getExperienceYears());
//        interviewer.setSpecialization(dto.getSpecialization());
//        interviewer.setGithubUrl(dto.getGithubUrl());
//        interviewer.setLinkedinUrl(dto.getLinkedinUrl());
//        interviewer.setProfilePicture(dto.getProfilePicture());
//        if (dto.getStatus() != null) interviewer.setStatus(dto.getStatus());
//
//        interviewerRepo.save(interviewer);
//        return "Interviewer profile updated successfully";
//    }
//
//    @Override
//    public String deleteInterviewerProfile(Long interviewerId, String username) {
//        Interviewer interviewer = interviewerRepo.findById(interviewerId)
//                .orElseThrow(() -> new RuntimeException("Interviewer not found"));
//
//        interviewerRepo.delete(interviewer);
//        return "Interviewer profile deleted successfully";
//    }
//
//    @Override
//    public List<InterviewerResponseDTO> getAllInterviewers() {
//        return interviewerRepo.findAll()
//                .stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }
//
//    // Helper method
//    private InterviewerResponseDTO mapToDTO(Interviewer interviewer) {
//        return new InterviewerResponseDTO(
//                interviewer.getInterviewerId(),
//                interviewer.getAuth().getUsername(),
//                interviewer.getAuth().getEmail(),
//                interviewer.getBio(),
//                interviewer.getCompany(),
//                interviewer.getDesignation(),
//                interviewer.getExperienceYears(),
//                interviewer.getSpecialization(),
//                interviewer.getGithubUrl(),
//                interviewer.getLinkedinUrl(),
//                interviewer.getProfilePicture(),
//                interviewer.getStatus(),
//                interviewer.getJoinSDate()
//        );
//    }
//}


package lk.ijse.springbootbackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewerServiceImpl implements InterviewerService {

    private final InterviewerRepo interviewerRepo;
    private final AuthRepo authRepo;
    private final Cloudinary cloudinary; // Cloudinary inject කළා

    @Override
    @Transactional
    public String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, MultipartFile imageFile, String username) {
        Auth auth = authRepo.findByUsername(username)
                .or(() -> authRepo.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        if (interviewerRepo.existsByAuth(auth)) {
            throw new RuntimeException("Interviewer profile already exists");
        }

        Interviewer interviewer = new Interviewer();
        interviewer.setAuth(auth);
        interviewer.setBio(dto.getBio());
        interviewer.setCompany(dto.getCompany());
        interviewer.setDesignation(dto.getDesignation());
        interviewer.setExperienceYears(dto.getExperienceYears());
        interviewer.setSpecialization(dto.getSpecialization());
        interviewer.setGithubUrl(dto.getGithubUrl());
        interviewer.setLinkedinUrl(dto.getLinkedinUrl());
        interviewer.setStatus("PENDING");

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "colloq_profiles/interviewers"));

                String imageUrl = uploadResult.get("url").toString();
                interviewer.setProfilePicture(imageUrl);
                auth.setProfilePic(imageUrl);

            } else {
                interviewer.setProfilePicture(dto.getProfilePicture());
                if (dto.getProfilePicture() != null) {
                    auth.setProfilePic(dto.getProfilePicture());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }

        interviewerRepo.save(interviewer);
        authRepo.save(auth);

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

    // InterviewerServiceImpl.java

    @Override
    @Transactional // 💡 අනිවාර්යයෙන්ම Transactional දාන්න Table දෙකක් Update කරන නිසා
    public String updateInterviewerProfile(CompleteInterviewerProfileDTO dto, MultipartFile imageFile, String username) {

        // 1. කලින් ඉන්න Auth user සහ Interviewer profile එක හොයාගන්න
        // Username හෝ Email දෙකෙන්ම හොයන්න ඉඩ දෙමු
        Auth auth = authRepo.findByUsername(username)
                .or(() -> authRepo.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        // 2. මූලික විස්තර Update කිරීම
        interviewer.setBio(dto.getBio());
        interviewer.setCompany(dto.getCompany());
        interviewer.setDesignation(dto.getDesignation());
        interviewer.setExperienceYears(dto.getExperienceYears());
        interviewer.setSpecialization(dto.getSpecialization());
        interviewer.setGithubUrl(dto.getGithubUrl());
        interviewer.setLinkedinUrl(dto.getLinkedinUrl());

        // 3. Image එකක් එවලා තියෙනවා නම් Cloudinary එකට Upload කිරීම
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                        ObjectUtils.asMap("folder", "colloq_profiles/interviewers"));

                String imageUrl = uploadResult.get("url").toString();

                // ✅ Interviewer Table එක Update කිරීම
                interviewer.setProfilePicture(imageUrl);

                // ✅ Auth Table එක Update කිරීම (Header එකේ පේන්න)
                auth.setProfilePic(imageUrl);

            } else if (dto.getProfilePicture() != null) {
                // Image එකක් එවලා නැත්නම්, DTO එකේ තියෙන URL එකම තියාගන්න
                interviewer.setProfilePicture(dto.getProfilePicture());
                auth.setProfilePic(dto.getProfilePicture());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile picture: " + e.getMessage());
        }

        // 4. දෙකම Save කිරීම
        interviewerRepo.save(interviewer);
        authRepo.save(auth);

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

    private InterviewerResponseDTO mapToDTO(Interviewer interviewer) {
        InterviewerResponseDTO dto = new InterviewerResponseDTO();

        dto.setInterviewerId(interviewer.getInterviewerId());

        // Auth Check
        if (interviewer.getAuth() != null) {
            dto.setUsername(interviewer.getAuth().getUsername());
            dto.setEmail(interviewer.getAuth().getEmail());
        } else {
            dto.setUsername("N/A");
            dto.setEmail("N/A");
        }

        dto.setBio(interviewer.getBio() != null ? interviewer.getBio() : "");
        dto.setCompany(interviewer.getCompany() != null ? interviewer.getCompany() : "");
        dto.setDesignation(interviewer.getDesignation() != null ? interviewer.getDesignation() : "");

        // 💡 Integer Handle
        dto.setExperienceYears(interviewer.getExperienceYears() != null ? interviewer.getExperienceYears() : 0);

        dto.setSpecialization(interviewer.getSpecialization() != null ? interviewer.getSpecialization() : "");
        dto.setGithubUrl(interviewer.getGithubUrl() != null ? interviewer.getGithubUrl() : "");
        dto.setLinkedinUrl(interviewer.getLinkedinUrl() != null ? interviewer.getLinkedinUrl() : "");
        dto.setProfilePicture(interviewer.getProfilePicture() != null ? interviewer.getProfilePicture() : "");
        dto.setStatus(interviewer.getStatus() != null ? interviewer.getStatus() : "PENDING");

        // 💡 joinSDate එක Date එකක් නම් String එකකට convert කරන්න
        if (interviewer.getJoinSDate() != null) {
            dto.setJoinSDate(interviewer.getJoinSDate().toString());
        } else {
            dto.setJoinSDate("");
        }

        return dto;
    }

    @Override
    public List<InterviewerResponseDTO> getPendingInterviewers() {
        System.out.println("DEBUG: Method started..."); // මේක වැටෙන්නම ඕනේ

        List<Interviewer> all = interviewerRepo.findAll();
        System.out.println("DEBUG: Total interviewers found in DB: " + all.size());

        // 💡 මෙතනදී status එක print කරලා බලමු DB එකේ තියෙන විදිහ
        for (Interviewer i : all) {
            System.out.println("ID: " + i.getInterviewerId() + " | Status in DB: [" + i.getStatus() + "]");
        }

        List<Interviewer> pending = all.stream()
                .filter(i -> i.getStatus() != null && i.getStatus().trim().equalsIgnoreCase("PENDING"))
                .collect(Collectors.toList());

        System.out.println("DEBUG: Filtered Pending Count: " + pending.size());

        return pending.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String approveInterviewer(Long interviewerId) {
        Interviewer interviewer = interviewerRepo.findById(interviewerId)
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        interviewer.setStatus("ACTIVE"); // Status එක ACTIVE කරනවා
        interviewerRepo.save(interviewer);

        return "Interviewer approved successfully";
    }
}
