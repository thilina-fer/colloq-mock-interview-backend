package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.LevelDTO;
import lk.ijse.springbootbackend.service.LevelService;
//import lk.ijse.springbootbackend.util.APIResponse; // 💡 ඔයාගේ APIResponse එක තියෙන තැනින් import කරගන්න
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/levels")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    public ResponseEntity<APIResponse> createLevel(@RequestBody LevelDTO dto) {
        String response = levelService.createLevel(dto);
        return new ResponseEntity<>(
                new APIResponse(201, "Level created successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    public ResponseEntity<APIResponse> getAllLevels() {
        List<LevelDTO> allLevels = levelService.getAllLevels();
        return new ResponseEntity<>(
                new APIResponse(200, "Success", allLevels),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getLevelById(@PathVariable Long id) {
        LevelDTO level = levelService.getLevelById(id);
        return new ResponseEntity<>(
                new APIResponse(200, "Level found", level),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateLevel(
            @PathVariable("id") Long id, // 👈 මෙතන ("id") කියලා අනිවාර්යයෙන්ම දාන්න
            @RequestBody LevelDTO dto
    ) {
        String response = levelService.updateLevel(id, dto);
        return new ResponseEntity<>(
                new APIResponse(200, "Level updated successfully", response),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteLevel(@PathVariable("id") Long id) { // 👈 මෙතනත් ("id")
        String response = levelService.deleteLevel(id);
        return new ResponseEntity<>(
                new APIResponse(200, "Level deleted successfully", response),
                HttpStatus.OK
        );
    }
}