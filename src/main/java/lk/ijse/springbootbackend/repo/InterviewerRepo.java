package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewerRepo extends JpaRepository<Interviewer, Long> {
        boolean existsByAuth(Auth auth);
        Optional<Interviewer> findByAuth(Auth auth);
        List<Interviewer> findByStatus(String status);
        Optional<Interviewer> findByAuth_Username(String username);
}
