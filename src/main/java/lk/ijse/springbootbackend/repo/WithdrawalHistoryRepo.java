package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.WithdrawalHistory;
import lk.ijse.springbootbackend.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalHistoryRepo extends JpaRepository<WithdrawalHistory, Long> {
    List<WithdrawalHistory> findAllByInterviewerOrderByWithdrawalDateDesc(Interviewer interviewer);
}