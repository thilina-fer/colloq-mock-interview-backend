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

        if (levelRepo.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Level already exists");
        }

        Level level = modelMapper.map(dto, Level.class);

        // default value handling (important)
        if (level.getStatus() == null) {
            level.setStatus("ACTIVE");
        }

        levelRepo.save(level);

        return "Level created successfully";
    }

    @Override
    public List<LevelDTO> getAllLevels() {

        return levelRepo.findAll()
                .stream()
                .map(level -> modelMapper.map(level, LevelDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LevelDTO getLevelById(Long id) {

        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        return modelMapper.map(level, LevelDTO.class);
    }

    @Override
    public String updateLevel(Long id, LevelDTO dto) {
        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        // නම වෙනස් කරනවා නම්, අලුත් නම දැනටමත් වෙනත් record එකකට තියෙනවාද බලන්න
        levelRepo.findByName(dto.getName()).ifPresent(existingLevel -> {
            if (!existingLevel.getLevelId().equals(id)) {
                throw new RuntimeException("Level name already exists");
            }
        });

        modelMapper.map(dto, level);
        level.setLevelId(id); // ID එක overwrite වීම වැළැක්වීමට
        levelRepo.save(level);
        return "Level updated successfully";
    }

    @Override
    public String deleteLevel(Long id) {

        Level level = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found"));

        levelRepo.delete(level);

        return "Level deleted successfully";
    }
}