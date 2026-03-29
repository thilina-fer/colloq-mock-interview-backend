package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.InterviewerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewerAvailabilityRepo extends JpaRepository<InterviewerAvailability, Long> {
    List<InterviewerAvailability> findByInterviewer_InterviewerId(Long interviewerId);
}