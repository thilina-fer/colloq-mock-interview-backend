package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InterviewerService {
    String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, org.springframework.web.multipart.MultipartFile image, String username);
    InterviewerResponseDTO getInterviewerProfile(String username);
    String updateInterviewerProfile(CompleteInterviewerProfileDTO dto, MultipartFile imageFile, String username);
    String deleteInterviewerProfile(Long interviewerId, String username);
    List<InterviewerResponseDTO> getAllInterviewers();
}
