package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.InterviewerAvailabilityDTO;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.entity.InterviewerAvailability;
import lk.ijse.springbootbackend.repo.InterviewerAvailabilityRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.service.InterviewerAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InterviewerAvailabilityImpl implements InterviewerAvailabilityService {

    private final InterviewerAvailabilityRepo availabilityRepo;
    private final InterviewerRepo interviewerRepo;
    private final ModelMapper modelMapper;

    @Override
    public String saveAvailability(InterviewerAvailabilityDTO dto) {
        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        // 2. Map and Save
        InterviewerAvailability availability = modelMapper.map(dto, InterviewerAvailability.class);
        availability.setInterviewer(interviewer);
        availability.setBooked(false); // Default false

        availabilityRepo.save(availability);
        return "Availability slot saved successfully";
    }

    @Override
    public List<InterviewerAvailabilityDTO> getAllAvailabilities() {
        return availabilityRepo.findAll().stream()
                .map(s -> modelMapper.map(s, InterviewerAvailabilityDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String updateAvailability(Long id, InterviewerAvailabilityDTO dto) {
        InterviewerAvailability existing = availabilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // දත්ත update කිරීම
        existing.setDate(dto.getDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());

        availabilityRepo.save(existing);
        return "Slot updated successfully";
    }

    @Override
    public String deleteAvailability(Long id) {
        if (!availabilityRepo.existsById(id)) {
            throw new RuntimeException("Slot not found");
        }
        availabilityRepo.deleteById(id);
        return "Slot deleted successfully";
    }
}