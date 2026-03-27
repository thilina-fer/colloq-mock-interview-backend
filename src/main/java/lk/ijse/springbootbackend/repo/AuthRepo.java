package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthRepo extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUsername(String username);
    Optional<Auth> findByEmail(String email);
    Optional<Auth> findByGoogleId(String googleId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}