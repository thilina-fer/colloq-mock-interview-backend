package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.PaymentDto;
import lk.ijse.springbootbackend.entity.BookingStatus;
import lk.ijse.springbootbackend.entity.Bookings;
import lk.ijse.springbootbackend.entity.Payments;
import lk.ijse.springbootbackend.repo.BookingRepo;
import lk.ijse.springbootbackend.repo.PaymentRepo;
import lk.ijse.springbootbackend.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
    private final GoogleCalendarService googleCalendarService;

    // 💡 Lombok අවුල් එන්නේ නැති වෙන්න අපිම හදපු Constructor එක
    public PaymentServiceImpl(PaymentRepo paymentRepo, BookingRepo bookingRepo, GoogleCalendarService googleCalendarService) {
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
        this.googleCalendarService = googleCalendarService;
    }

    @Override
    public String processPayment(PaymentDto paymentDto) {

        // 1. Booking එක Database එකෙන් හොයාගන්නවා
        Bookings booking = bookingRepo.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        try {
            // 2. Booking එකේ තියෙන දවස සහ වෙලාව Google Meet එකට තේරෙන විදිහට හදාගන්නවා
            LocalDateTime startDateTime = LocalDateTime.of(booking.getAvailability().getDate(), booking.getAvailability().getStartTime());
            LocalDateTime endDateTime = LocalDateTime.of(booking.getAvailability().getDate(), booking.getAvailability().getEndTime());

            Date startDate = Date.from(startDateTime.atZone(ZoneId.of("Asia/Colombo")).toInstant());
            Date endDate = Date.from(endDateTime.atZone(ZoneId.of("Asia/Colombo")).toInstant());

            // 3. Meet Link එක හදනවා
            String title = "ColloQ Interview";
            String description = "Mock Interview Session.";
            String meetLink = googleCalendarService.createMeetLink(title, description, startDate, endDate);

            // 💡 අලුතින් එකතු කළ කොටස: Booking එකට Link එක දාලා DB එකේ Update කරනවා
            booking.setMeetingLink(meetLink);
            booking.setStatus(BookingStatus.BOOKED); // Booking එක Confirm කරනවා
            bookingRepo.save(booking);

            // 4. Payment එක Database එකේ Save කරනවා
            Payments payment = new Payments();
            payment.setAmount(paymentDto.getAmount());
            payment.setPaymentMethod(paymentDto.getPaymentMethod());
            payment.setTransactionId(paymentDto.getTransactionId());
            payment.setStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setBookings(booking);
            paymentRepo.save(payment);

            return "Payment Successful! Meet Link: " + meetLink;

        } catch (Exception e) {
            e.printStackTrace();
            return "Payment processed, but failed to generate Meet Link: " + e.getMessage();
        }
    }
}