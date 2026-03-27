package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.InterviewerAvailabilityDTO;
import java.util.List;

public interface InterviewerAvailabilityService {
    String saveAvailability(InterviewerAvailabilityDTO dto);
    List<InterviewerAvailabilityDTO> getAllAvailabilities();
    String updateAvailability(Long id, InterviewerAvailabilityDTO dto);
    String deleteAvailability(Long id);

}