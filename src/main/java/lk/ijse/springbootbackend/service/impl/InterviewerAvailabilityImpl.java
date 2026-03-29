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


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InterviewerAvailabilityImpl implements InterviewerAvailabilityService {

    private final InterviewerAvailabilityRepo availabilityRepo;
    private final InterviewerRepo interviewerRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public String saveAvailabilityBatch(List<InterviewerAvailabilityDTO> dtos) {
        if (dtos.isEmpty()) return "No slots to save";

        // 1. Interviewer ඉන්නවාද කියලා බලනවා (පළමු DTO එකේ ID එකෙන්)
        Interviewer interviewer = interviewerRepo.findById(dtos.get(0).getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Interviewer not found"));

        // 2. Frontend එකෙන් එන "09:00 AM" format එකට ගැලපෙන Formatter එක
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        try {
            List<InterviewerAvailability> entities = dtos.stream().map(dto -> {
                InterviewerAvailability availability = new InterviewerAvailability();

                // 💡 String -> LocalDate (Standard "YYYY-MM-DD" format)
                availability.setDate(LocalDate.parse(dto.getDate()));

                // 💡 String -> LocalTime ("09:00 AM" -> LocalTime object)
                availability.setStartTime(LocalTime.parse(dto.getStartTime(), timeFormatter));
                availability.setEndTime(LocalTime.parse(dto.getEndTime(), timeFormatter));

                availability.setInterviewer(interviewer);
                availability.setBooked(false); // අලුත් ඒව හැමතිස්සෙම Available

                return availability;
            }).collect(Collectors.toList());

            availabilityRepo.saveAll(entities);
            return "Batch availability saved successfully!";

        } catch (Exception e) {
            throw new RuntimeException("Error saving slots: " + e.getMessage());
        }
    }

    @Override
    public List<InterviewerAvailabilityDTO> getAllAvailabilities() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        Long currentInterviewerId = 1L;

        return availabilityRepo.findByInterviewer_InterviewerId(currentInterviewerId).stream()
                .map(s -> {
                    InterviewerAvailabilityDTO dto = new InterviewerAvailabilityDTO();
                    dto.setAvailabilityId(s.getAvailabilityId());

                    // ✅ Entity (LocalDate) -> DTO (String) බවට හරවනවා
                    if (s.getDate() != null) {
                        dto.setDate(s.getDate().toString());
                    }

                    // ✅ Entity (LocalTime) -> DTO (String) බවට හරවනවා
                    if (s.getStartTime() != null) {
                        dto.setStartTime(s.getStartTime().format(timeFormatter));
                    }

                    if (s.getEndTime() != null) {
                        dto.setEndTime(s.getEndTime().format(timeFormatter));
                    }

                    dto.setBooked(s.isBooked());

                    if (s.getInterviewer() != null) {
                        dto.setInterviewerId(s.getInterviewer().getInterviewerId());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteAvailability(Long id) {
        // 1. Slot එක තියෙනවාද බලනවා
        InterviewerAvailability availability = availabilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found with ID: " + id));

        // 2. 💡 වැදගත්: Book කරපු slot එකක් delete කරන්න දෙන්න එපා
        if (availability.isBooked()) {
            throw new RuntimeException("Cannot delete! This slot is already booked by a candidate.");
        }

        availabilityRepo.delete(availability);
        return "Availability slot deleted successfully!";
    }
}