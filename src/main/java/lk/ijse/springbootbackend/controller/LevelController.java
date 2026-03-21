package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.LevelDTO;
import lk.ijse.springbootbackend.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/level")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LevelController {

    private final LevelService levelService;

    // Create Level
    @PostMapping
    public ResponseEntity<String> createLevel(@RequestBody LevelDTO dto) {
        String response = levelService.createLevel(dto);
        return ResponseEntity.ok(response);
    }

    // Get All Levels
    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        return ResponseEntity.ok(levelService.getAllLevels());
    }

    // Get Level By ID
    @GetMapping("/{id}")
    public ResponseEntity<LevelDTO> getLevelById(@PathVariable Long id) {
        return ResponseEntity.ok(levelService.getLevelById(id));
    }

    // Update Level
    @PutMapping("/{id}")
    public ResponseEntity<String> updateLevel(
            @PathVariable Long id,
            @RequestBody LevelDTO dto) {

        String response = levelService.updateLevel(id, dto);
        return ResponseEntity.ok(response);
    }

    // Delete Level
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLevel(@PathVariable Long id) {
        String response = levelService.deleteLevel(id);
        return ResponseEntity.ok(response);
    }
}
