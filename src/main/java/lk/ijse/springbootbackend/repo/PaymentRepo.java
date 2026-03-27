package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payments, Long> {
}