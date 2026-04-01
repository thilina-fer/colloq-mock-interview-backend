package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.BankAccount;
import lk.ijse.springbootbackend.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByInterviewer(Interviewer interviewer);
    boolean existsByInterviewer(Interviewer interviewer);

}