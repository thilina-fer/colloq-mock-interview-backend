package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;

public interface InterviewerService {
    String completeInterviewerProfile(CompleteInterviewerProfileDTO dto, String username);
}
