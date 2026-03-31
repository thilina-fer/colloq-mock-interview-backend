package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.BankAccount;
import lk.ijse.springbootbackend.entity.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, Long> {

    // Interviewer object එක හරහා අදාළ බැංකු ගිණුම සෙවීමට
    Optional<BankAccount> findByInterviewer(Interviewer interviewer);

    // Interviewer ට දැනටමත් ගිණුමක් තිබේදැයි බැලීමට (Boolean return කරයි)
    boolean existsByInterviewer(Interviewer interviewer);

}