package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Candidate;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CandidateRepo extends CrudRepository<Candidate, Long> {
    Optional<Candidate> findByAuth(Auth auth);
    boolean existsByAuth(Auth auth);
}
