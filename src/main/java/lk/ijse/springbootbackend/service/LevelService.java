package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.LevelDTO;

import java.util.List;

public interface LevelService {

    String createLevel(LevelDTO dto);

    List<LevelDTO> getAllLevels();

    LevelDTO getLevelById(Long id);

    String updateLevel(Long id, LevelDTO dto);

    String deleteLevel(Long id);
}
