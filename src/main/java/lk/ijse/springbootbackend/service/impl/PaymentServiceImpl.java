package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.PaymentDto;
import lk.ijse.springbootbackend.entity.BookingStatus;
import lk.ijse.springbootbackend.entity.Bookings;
import lk.ijse.springbootbackend.entity.Payments;
import lk.ijse.springbootbackend.repo.BookingRepo;
import lk.ijse.springbootbackend.repo.PaymentRepo;
import lk.ijse.springbootbackend.service.PaymentService;
import lk.ijse.springbootbackend.service.WalletService;
import lk.ijse.springbootbackend.service.impl.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
    private final WalletService walletService;
    private final GoogleCalendarService googleCalendarService;

    @Override
    public String processPayment(PaymentDto paymentDto) {
        Bookings booking = bookingRepo.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isPaid()) {
            throw new RuntimeException("Already paid.");
        }

        Double actualPrice = booking.getLevel().getPrice();

        //Payment Record
        Payments payment = new Payments();
        payment.setAmount(actualPrice);
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("SUCCESS");
        payment.setBookings(booking);
        paymentRepo.save(payment);

        // 2.Google Meet Link
        try {
            java.util.Date startDate = java.sql.Timestamp.valueOf(LocalDateTime.of(booking.getAvailability().getDate(), booking.getAvailability().getStartTime()));
            java.util.Date endDate = java.sql.Timestamp.valueOf(LocalDateTime.of(booking.getAvailability().getDate(), booking.getAvailability().getEndTime()));

            String meetLink = googleCalendarService.createMeetLink(
                    "Interview: " + booking.getJobType(),
                    "ColloQ Mock Interview Session",
                    startDate, endDate
            );
            booking.setMeetingLink(meetLink);
        } catch (Exception e) {
            booking.setMeetingLink("Link Generation Failed");
        }

        // 3. Booking Update
        booking.setPaid(true);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepo.save(booking);

        walletService.processTransaction(
                booking.getBookingId(),
                booking.getInterviewer().getInterviewerId(),
                actualPrice
        );

        return "Payment successful! Wallet updated & Meet link generated.";
    }
}