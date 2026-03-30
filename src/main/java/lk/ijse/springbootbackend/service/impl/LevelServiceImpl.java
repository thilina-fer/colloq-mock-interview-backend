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
        // 1. නම දැනටමත් තියෙනවද බලන්න (Case-insensitive)
        if (levelRepo.findByName(dto.getName().toUpperCase()).isPresent()) {
            throw new RuntimeException("Level name already exists: " + dto.getName());
        }

        // 2. DTO එක Entity එකකට Map කිරීම
        Level level = new Level();
        level.setName(dto.getName().toUpperCase()); // ලෙවල් නම හැමතිස්සෙම Capital තිබීම හොඳයි (INTERN, SENIOR)
        level.setSessionDuration(dto.getSessionDuration());
        level.setPrice(dto.getPrice());

        // 3. Status එක default "ACTIVE" කිරීම
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            level.setStatus("ACTIVE");
        } else {
            level.setStatus(dto.getStatus());
        }

        // 4. Save කිරීම
        levelRepo.save(level);

        return "Level created successfully";
    }

    @Override
    @Transactional // 💡 Transactional පාවිච්චි කිරීමෙන් DB integrity එක ආරක්ෂා වේ
    public String updateLevel(Long id, LevelDTO dto) {
        // 1. පරණ Level එක තියෙනවද බලන්න
        Level existingLevel = levelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found with ID: " + id));

        // 2. නම වෙනස් කරනවා නම්, ඒ නමින් වෙනත් Level එකක් දැනටමත් තියෙනවද බලන්න
        // 💡 මෙතන trim() සහ toUpperCase() පාවිච්චි කිරීමෙන් " intern" වගේ වැරදි මගහැරේ
        String newName = dto.getName().trim().toUpperCase();

        levelRepo.findByName(newName).ifPresent(l -> {
            if (!l.getLevelId().equals(id)) {
                throw new RuntimeException("The level name '" + newName + "' is already taken by another tier.");
            }
        });

        // 3. අගයන් Update කිරීම
        existingLevel.setName(newName);

        // 💡 Null safety: Frontend එකෙන් වැරදිලාවත් null ආවොත් පරණ අගයම තියාගන්න
        if (dto.getSessionDuration() != null) {
            existingLevel.setSessionDuration(dto.getSessionDuration());
        }

        if (dto.getPrice() != null) {
            existingLevel.setPrice(dto.getPrice());
        }

        if (dto.getStatus() != null) {
            existingLevel.setStatus(dto.getStatus());
        }

        // 4. Save කිරීම
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

            // ✅ මේ පේළි දෙක අලුතින් එකතු කරන්න:
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