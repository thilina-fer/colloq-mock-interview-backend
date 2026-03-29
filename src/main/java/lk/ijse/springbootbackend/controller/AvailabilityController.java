package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.InterviewerAvailabilityDTO;
import lk.ijse.springbootbackend.service.InterviewerAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class AvailabilityController {

    private final InterviewerAvailabilityService availabilityService;

    @PostMapping("/batch-save")
    public ResponseEntity<String> saveBatch(@RequestBody List<InterviewerAvailabilityDTO> dtos) {
        return ResponseEntity.ok(availabilityService.saveAvailabilityBatch(dtos));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<InterviewerAvailabilityDTO>> getAll() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.deleteAvailability(id));
    }
}