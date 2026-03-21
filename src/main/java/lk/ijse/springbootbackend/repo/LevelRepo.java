package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelRepo extends JpaRepository<Level, Long> {

    Optional<Level> findByName(String name);
}