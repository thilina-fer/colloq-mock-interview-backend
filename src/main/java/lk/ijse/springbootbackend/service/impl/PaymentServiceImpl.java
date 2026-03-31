//package lk.ijse.springbootbackend.service.impl;
//
//import lk.ijse.springbootbackend.dto.PaymentDto;
//import lk.ijse.springbootbackend.entity.Bookings;
//import lk.ijse.springbootbackend.entity.Interviewer;
//import lk.ijse.springbootbackend.entity.Payments;
//import lk.ijse.springbootbackend.repo.BookingRepo;
//import lk.ijse.springbootbackend.repo.InterviewerRepo;
//import lk.ijse.springbootbackend.repo.PaymentRepo;
//import lk.ijse.springbootbackend.service.PaymentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional // 🎯 Atomic transaction එකක් විදිහට වැඩ කිරීමට (Critical for payments)
//public class PaymentServiceImpl implements PaymentService {
//
//    private final PaymentRepo paymentRepo;
//    private final BookingRepo bookingRepo;
//    private final InterviewerRepo interviewerRepo;
//
//    @Override
//    public String processPayment(PaymentDto paymentDto) {
//
//        // 1. අදාළ Booking එක පරීක්ෂා කිරීම
//        Bookings booking = bookingRepo.findById(paymentDto.getBookingId())
//                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + paymentDto.getBookingId()));
//
//        if (booking.isPaid()) {
//            throw new RuntimeException("This session has already been paid for.");
//        }
//
//        // 2. Payment Record එක නිර්මාණය කර Save කිරීම
//        Payments payment = new Payments();
//        payment.setAmount(paymentDto.getAmount());
//        payment.setPaymentMethod(paymentDto.getPaymentMethod());
//
//        // Mock Transaction ID එකක් generate කිරීම
//        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//        payment.setTransactionId(txnId);
//
//        payment.setStatus("SUCCESS");
//        payment.setPaymentDate(LocalDateTime.now());
//        payment.setBookings(booking);
//
//        paymentRepo.save(payment);
//
//        // 3. Booking එක Update කිරීම (Paid status & Meeting Link)
//        booking.setPaid(true);
//
//        // Mock Meeting Link එකක් ලබා දීම
//        String mockLink = "https://meet.google.com/colloq-" + UUID.randomUUID().toString().substring(0, 4) + "-" + UUID.randomUUID().toString().substring(0, 4);
//        booking.setMeetingLink(mockLink);
//
//        bookingRepo.save(booking);
//
//        // 4. 🎯 Interviewer ගේ Wallet එකට මුදල එකතු කිරීම
//        Interviewer interviewer = booking.getInterviewer();
//        if (interviewer != null) {
//            Double currentBalance = interviewer.getWalletBalance() != null ? interviewer.getWalletBalance() : 0.0;
//            interviewer.setWalletBalance(currentBalance + paymentDto.getAmount());
//            interviewerRepo.save(interviewer);
//        } else {
//            throw new RuntimeException("Interviewer not found for this booking.");
//        }
//
//        return "Payment processed successfully. Transaction ID: " + txnId;
//    }
//}

package lk.ijse.springbootbackend.service.impl;

import lk.ijse.springbootbackend.dto.PaymentDto;
import lk.ijse.springbootbackend.entity.Bookings;
import lk.ijse.springbootbackend.entity.Interviewer;
import lk.ijse.springbootbackend.entity.Payments;
import lk.ijse.springbootbackend.repo.BookingRepo;
import lk.ijse.springbootbackend.repo.InterviewerRepo;
import lk.ijse.springbootbackend.repo.PaymentRepo;
import lk.ijse.springbootbackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
    private final InterviewerRepo interviewerRepo;
    private final GoogleCalendarService googleCalendarService; // 🎯 Inject Google Calendar Service

    @Override
    public String processPayment(PaymentDto paymentDto) {

        // 1. අදාළ Booking එක පරීක්ෂා කිරීම
        Bookings booking = bookingRepo.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + paymentDto.getBookingId()));

        if (booking.isPaid()) {
            throw new RuntimeException("This session has already been paid for.");
        }

        // 2. Google Calendar Event එක සහ Meeting Link එක සාදා ගැනීම
        String meetLink;
        try {
            // DB එකේ තියෙන String Date/Time ටික java.util.Date වලට හරවනවා
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDateTime = formatter.parse(booking.getAvailability().getDate() + " " + booking.getAvailability().getStartTime());
            Date endDateTime = formatter.parse(booking.getAvailability().getDate() + " " + booking.getAvailability().getEndTime());

            // Google Calendar Service එක call කරනවා
            meetLink = googleCalendarService.createMeetLink(
                    "ColloQ Interview: " + booking.getJobType(),
                    "Technical Mock Interview with " + booking.getInterviewer().getAuth().getUsername(),
                    startDateTime,
                    endDateTime
            );

            // මොකක් හරි හේතුවකින් ලින්ක් එක ආවේ නැත්නම් error එකක් දාමු
            if (meetLink == null || meetLink.contains("Error") || meetLink.contains("failed")) {
                throw new RuntimeException("Failed to generate Google Meet link: " + meetLink);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Google Calendar integration failed: " + e.getMessage());
        }

        // 3. Payment Record එක නිර්මාණය කර Save කිරීම
        Payments payment = new Payments();
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());

        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setTransactionId(txnId);
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookings(booking);

        paymentRepo.save(payment);

        // 4. Booking එක Update කිරීම (Paid status & Real Meeting Link)
        booking.setPaid(true);
        booking.setMeetingLink(meetLink); // 🎯 දැන් මෙතනට වැටෙන්නේ ඇත්තම Google Meet ලින්ක් එක
        bookingRepo.save(booking);

        // 5. Interviewer ගේ Wallet එකට මුදල එකතු කිරීම
        Interviewer interviewer = booking.getInterviewer();
        if (interviewer != null) {
            Double currentBalance = interviewer.getWalletBalance() != null ? interviewer.getWalletBalance() : 0.0;
            interviewer.setWalletBalance(currentBalance + paymentDto.getAmount());
            interviewerRepo.save(interviewer);
        }

        return "Payment successful! Transaction ID: " + txnId;
    }
}