package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.SystemProfitDTO;
import java.util.List;

public interface SystemProfitService {
    void recordProfit(Long bookingId, Double profitAmount);
    Double getTotalProfit();
    List<SystemProfitDTO> getAllProfits();
}