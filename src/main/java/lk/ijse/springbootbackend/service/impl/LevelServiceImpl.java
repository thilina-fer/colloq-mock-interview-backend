package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.LevelDTO;
import lk.ijse.springbootbackend.entity.Level;
import lk.ijse.springbootbackend.repo.LevelRepo;
import lk.ijse.springbootbackend.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepo levelRepo;
    private final ModelMapper modelMapper;

    @Override
    public String createLevel(LevelDTO dto) {
        if (levelRepo.findByName(dto.getName().toUpperCase()).isPresent()) {
            throw new RuntimeException("Level name already exists: " + dto.getName());
        }

        Level level = new Level();
        level.setName(dto.getName().toUpperCase());
        level.setSessionDuration(dto.getSessionDuration());
        level.setPrice(dto.getPrice());

        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            level.setStatus("ACTIVE");
        } else {
            level.setStatus(dto.getStatus());
        }

        levelRepo.save(level);

        return "Level created successfully";
    }

    @Override
    @Transactional
    public String updateLevel(Long id, LevelDTO dto) {

        Level existingLevel = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found with ID: " + id));

        String newName = dto.getName().trim().toUpperCase();

        levelRepo.findByName(newName).ifPresent(l -> {
            if (!l.getLevelId().equals(id)) {
                throw new RuntimeException("The level name '" + newName + "' is already taken by another tier.");
            }
        });


        existingLevel.setName(newName);

        if (dto.getSessionDuration() != null) {
            existingLevel.setSessionDuration(dto.getSessionDuration());
        }

        if (dto.getPrice() != null) {
            existingLevel.setPrice(dto.getPrice());
        }

        if (dto.getStatus() != null) {
            existingLevel.setStatus(dto.getStatus());
        }

        levelRepo.save(existingLevel);

        return "Level updated successfully";
    }

    @Override
    public List<LevelDTO> getAllLevels() {
        List<Level> levels = levelRepo.findAll();

        return levels.stream().map(level -> {
            LevelDTO dto = new LevelDTO();
            dto.setLevelId(level.getLevelId());
            dto.setName(level.getName());
            dto.setSessionDuration(level.getSessionDuration());
            dto.setPrice(level.getPrice());
            dto.setStatus(level.getStatus());

            return dto;
        }).collect(Collectors.toList());
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