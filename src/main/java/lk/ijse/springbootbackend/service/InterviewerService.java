package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.interviewer.InterviewerResponseDTO;

import java.util.List;

public interface InterviewerService {
    String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, String username);
    InterviewerResponseDTO getInterviewerProfile(String username);
    String updateInterviewerProfile(CompleteInterviewerProfileDTO dto, String username);
    String deleteInterviewerProfile(Long interviewerId, String username);
    List<InterviewerResponseDTO> getAllInterviewers();
}
