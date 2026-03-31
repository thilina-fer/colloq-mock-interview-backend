package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.InterviewerAvailabilityDTO;
import java.util.List;
public interface InterviewerAvailabilityService {
    String saveAvailabilityBatch(List<InterviewerAvailabilityDTO> dtos, String username) ; // 💡 Batch Save
    public List<InterviewerAvailabilityDTO> getAllAvailabilities(String username);
    String deleteAvailability(Long id);
    List<InterviewerAvailabilityDTO> getAvailabilitiesByInterviewerId(Long interviewerId);
}