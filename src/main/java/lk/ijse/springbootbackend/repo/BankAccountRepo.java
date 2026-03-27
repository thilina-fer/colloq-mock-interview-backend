package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByInterviewer_InterviewerId(Long interviewerId);
}