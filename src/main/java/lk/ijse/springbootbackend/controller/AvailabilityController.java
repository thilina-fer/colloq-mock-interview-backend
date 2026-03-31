package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.InterviewerAvailabilityDTO;
import lk.ijse.springbootbackend.service.InterviewerAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // 🎯 මේක අනිවාර්යයෙන්ම import කරන්න
import java.util.List;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class AvailabilityController {

    private final InterviewerAvailabilityService availabilityService;

    // 🚀 1. Batch Save (Username එක pass කරනවා)
    @PostMapping("/batch-save")
    public ResponseEntity<String> saveBatch(
            @RequestBody List<InterviewerAvailabilityDTO> dtos,
            Principal principal // 🎯 Token එකෙන් Username එක මෙතනට එනවා
    ) {
        return ResponseEntity.ok(availabilityService.saveAvailabilityBatch(dtos, principal.getName()));
    }

    // 🚀 2. Get All (ලොග් වෙලා ඉන්න Interviewer ගේ slots විතරක් ගන්න)
    @GetMapping("/get-all")
    public ResponseEntity<List<InterviewerAvailabilityDTO>> getAll(Principal principal) {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities(principal.getName()));
    }

    // 🚀 3. Get By Interviewer ID (මේක Candidate පැත්තෙන් පාවිච්චි කරද්දී Principal අවශ්‍ය නැහැ)
    @GetMapping("/interviewer/{interviewerId}")
    public ResponseEntity<List<InterviewerAvailabilityDTO>> getByInterviewerId(
            @PathVariable("interviewerId") Long interviewerId
    ) {
        System.out.println("🚀 [Controller] Request received for Interviewer ID: " + interviewerId);
        List<InterviewerAvailabilityDTO> availabilityList = availabilityService.getAvailabilitiesByInterviewerId(interviewerId);
        return ResponseEntity.ok(availabilityList);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAvailability(@PathVariable("id") Long id) {
        try {
            String message = availabilityService.deleteAvailability(id);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}