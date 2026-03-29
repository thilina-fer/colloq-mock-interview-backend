package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.LevelDTO;
import lk.ijse.springbootbackend.entity.Level;
import lk.ijse.springbootbackend.repo.LevelRepo;
import lk.ijse.springbootbackend.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepo levelRepo;
    private final ModelMapper modelMapper;

    @Override
    public String createLevel(LevelDTO dto) {
        // නම case-insensitive විදිහට check කරන එක හොඳයි (Intern / intern)
        if (levelRepo.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Level name already exists");
        }

        Level level = modelMapper.map(dto, Level.class);
        if (level.getStatus() == null) {
            level.setStatus("ACTIVE");
        }

        levelRepo.save(level);
        return "Level created successfully";
    }

    @Override
    public String updateLevel(Long id, LevelDTO dto) {
        Level existingLevel = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));
        levelRepo.findByName(dto.getName()).ifPresent(l -> {
            if (!l.getLevelId().equals(id)) {
                throw new RuntimeException("Another level with this name already exists");
            }
        });
        existingLevel.setName(dto.getName());
        existingLevel.setSessionDuration(dto.getSessionDuration());
        existingLevel.setPrice(dto.getPrice());
        existingLevel.setStatus(dto.getStatus());

        levelRepo.save(existingLevel);
        return "Level updated successfully";
    }

    @Override
    public List<LevelDTO> getAllLevels() {
        System.out.println("--- [DEBUG] getAllLevels started ---");

        List<Level> levels = levelRepo.findAll();

        // 1. DB එකෙන් දත්ත ආවාද කියලා බලමු
        System.out.println("--- [DEBUG] DB levels count: " + levels.size());

        if (levels.isEmpty()) {
            System.out.println("--- [DEBUG] Warning: No levels found in Database! ---");
        }

        List<LevelDTO> dtos = levels.stream().map(level -> {
            // 2. එකින් එක Entity එකේ දත්ත print කරලා බලමු
            System.out.println("--- [DEBUG] Mapping Level: ID=" + level.getLevelId() + ", Name=" + level.getName());

            LevelDTO dto = new LevelDTO();
            dto.setLevelId(level.getLevelId());
            dto.setName(level.getName());
            return dto;
        }).collect(Collectors.toList());

        System.out.println("--- [DEBUG] Mapping completed. Returning " + dtos.size() + " DTOs ---");
        return dtos;
    }

    @Override
    public LevelDTO getLevelById(Long id) {

        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        return modelMapper.map(level, LevelDTO.class);
    }

    @Override
    public String deleteLevel(Long id) {

        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        levelRepo.delete(level);

        return "Level deleted successfully";
    }
}