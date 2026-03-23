package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Bookings;
import lk.ijse.springbootbackend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Bookings, Long> {
    // Interviewer ට තමන්ට ආපු පෙන්ඩින් බුකින්ස් බලාගන්න
    List<Bookings> findByInterviewer_InterviewerIdAndStatus(Long interviewerId, BookingStatus status);

    // Candidate ට තමන්ගේ බුකින්ස් හිස්ට්‍රිය බලාගන්න
    List<Bookings> findByCandidate_CandidateId(Long candidateId);
}