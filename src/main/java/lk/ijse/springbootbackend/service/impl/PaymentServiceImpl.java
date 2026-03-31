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
    @Transactional // 🎯 Database එකට දත්ත යන එක Safe කරන්න මේක දාන්න
    public String processPayment(PaymentDto paymentDto) {

        // 1. අදාළ Booking එක හොයාගන්න
        Bookings booking = bookingRepo.findById(paymentDto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isPaid()) {
            throw new RuntimeException("This booking is already paid.");
        }

        // 🎯 [CRITICAL FIX]: Frontend එකෙන් එවන amount එක වෙනුවට
        // DB එකේ තියෙන Interviewer ගේ price එකම ගන්න.
        Double actualPrice = booking.getLevel().getPrice();

        if (actualPrice == null || actualPrice <= 0) {
            // මොකක් හරි හේතුවකට price එක null නම් විතරක් DTO එකේ අගය ගන්න
            actualPrice = paymentDto.getAmount();
        }

        // 2. Payment Record එක සකස් කිරීම
        Payments payment = new Payments();
        payment.setAmount(actualPrice); // ✅ දැන් මෙතනට 10000.00 ම වැටෙනවා
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus("SUCCESS");
        payment.setBookings(booking);

        paymentRepo.save(payment);

        // 3. Booking එක Update කිරීම
        booking.setPaid(true);
        // ... මෙතන ඔයාගේ Google Meet Link generation logic එක තියෙන්න ඕනේ ...
        bookingRepo.save(booking);

        // 4. Interviewer ගේ Wallet එකට මුදල එකතු කිරීම
        Interviewer interviewer = booking.getInterviewer();
        if (interviewer != null) {
            Double currentBalance = (interviewer.getWalletBalance() != null) ? interviewer.getWalletBalance() : 0.0;
            interviewer.setWalletBalance(currentBalance + actualPrice); // ✅ Wallet එකටත් 10000 ම එකතු වෙනවා
            interviewerRepo.save(interviewer);
        }

        return "Payment successful! Amount Processed: LKR " + actualPrice;
    }
}