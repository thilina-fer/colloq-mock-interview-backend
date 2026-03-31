package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.InterviewerAvailabilityDTO;
import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.entity.InterviewerAvailability;
import lk.ijse.springbootbackend.repo.AuthRepo;
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
    private final AuthRepo authRepo;

    @Override
    @Transactional
    public String saveAvailabilityBatch(List<InterviewerAvailabilityDTO> dtos, String username) { // 🎯 Username එක parameter එකක් විදිහට ගන්න
        if (dtos.isEmpty()) return "No slots to save";

        // 1. [FIX]: Frontend එකෙන් එවන ID එක වෙනුවට Token එකේ ඉන්න User හරහා Interviewer ව හොයාගන්න
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        try {
            List<InterviewerAvailability> entities = dtos.stream().map(dto -> {
                InterviewerAvailability availability = new InterviewerAvailability();

                // String -> LocalDate
                availability.setDate(LocalDate.parse(dto.getDate()));

                // String -> LocalTime
                availability.setStartTime(LocalTime.parse(dto.getStartTime(), timeFormatter));
                availability.setEndTime(LocalTime.parse(dto.getEndTime(), timeFormatter));

                // 🎯 මෙතන දැන් නිවැරදි Interviewer (Amal ද Kamal ද කියලා) set වෙනවා
                availability.setInterviewer(interviewer);
                availability.setBooked(false);

                return availability;
            }).collect(Collectors.toList());

            availabilityRepo.saveAll(entities);
            return "Batch availability saved successfully!";

        } catch (Exception e) {
            throw new RuntimeException("Error saving slots: " + e.getMessage());
        }
    }

    @Override
    public List<InterviewerAvailabilityDTO> getAllAvailabilities(String username) { // 🎯 Username එක parameter එකක් විදිහට ගත්තා
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        // 1. [FIX]: Token එකෙන් එන Username එක හරහා ලොග් වෙලා ඉන්න Interviewer ව හොයාගන්න
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        Long currentInterviewerId = interviewer.getInterviewerId(); // 🎯 දැන් මේක Dynamic (1, 2, 3...)

        return availabilityRepo.findByInterviewer_InterviewerId(currentInterviewerId).stream()
                .map(s -> {
                    InterviewerAvailabilityDTO dto = new InterviewerAvailabilityDTO();
                    dto.setAvailabilityId(s.getAvailabilityId());

                    // ✅ Entity (LocalDate) -> DTO (String)
                    if (s.getDate() != null) {
                        dto.setDate(s.getDate().toString());
                    }

                    // ✅ Entity (LocalTime) -> DTO (String) "09:00 AM" format එකට
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

    @Override
    public List<InterviewerAvailabilityDTO> getAvailabilitiesByInterviewerId(Long interviewerId) {
        System.out.println("🔍 [Service] Fetching availability for ID: " + interviewerId);

        List<InterviewerAvailability> entities = availabilityRepo.findByInterviewer_InterviewerId(interviewerId);
        System.out.println("📦 [Service] Database found " + entities.size() + " records.");

        return entities.stream()
                .map(availability -> {
                    System.out.println("🔄 [Service] Mapping Slot ID: " + availability.getAvailabilityId());
                    return modelMapper.map(availability, InterviewerAvailabilityDTO.class);
                })
                .collect(Collectors.toList());
    }
}