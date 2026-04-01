package lk.ijse.springbootbackend.repo;

import lk.ijse.springbootbackend.entity.SystemProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemProfitRepo extends JpaRepository<SystemProfit, Long> {
    @Query("SELECT SUM(s.amount) FROM SystemProfit s")
    Double getTotalSystemProfit();
}