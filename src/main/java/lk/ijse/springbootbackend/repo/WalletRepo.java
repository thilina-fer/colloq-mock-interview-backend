package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByInterviewer(Interviewer interviewer);
    Optional<Wallet> findByInterviewer_InterviewerId(Long interviewerId);
    @Query("SELECT w FROM Wallet w WHERE w.interviewer.auth.username = :username")
    Optional<Wallet> findByInterviewerUsername(@Param("username") String username);
}