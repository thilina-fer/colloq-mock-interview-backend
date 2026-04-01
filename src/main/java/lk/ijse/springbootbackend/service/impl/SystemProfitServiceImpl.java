package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.SystemProfitDTO;
import lk.ijse.springbootbackend.entity.Bookings;
import lk.ijse.springbootbackend.entity.SystemProfit;
import lk.ijse.springbootbackend.repo.BookingRepo;
import lk.ijse.springbootbackend.repo.SystemProfitRepo;
import lk.ijse.springbootbackend.service.SystemProfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemProfitServiceImpl implements SystemProfitService {

    private final SystemProfitRepo systemProfitRepo;
    private final BookingRepo bookingRepo;

    @Override
    public void recordProfit(Long bookingId, Double profitAmount) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        SystemProfit profit = new SystemProfit();
        profit.setAmount(profitAmount);
        profit.setReceivedDate(LocalDateTime.now());


        systemProfitRepo.save(profit);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalProfit() {
        return systemProfitRepo.getTotalSystemProfit();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemProfitDTO> getAllProfits() {
        return systemProfitRepo.findAll().stream()
                .map(p -> new SystemProfitDTO(
                        p.getProfitId(),
                        p.getAmount(),
                        p.getReceivedDate(),
                        null
                ))
                .collect(Collectors.toList());
    }
}