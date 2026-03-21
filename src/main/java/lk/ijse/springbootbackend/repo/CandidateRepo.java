package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CandidateRepo extends JpaRepository<Candidate, Long> {
    boolean existsByAuth(Auth auth);
    Optional<Candidate> findByAuth(Auth auth);
}
