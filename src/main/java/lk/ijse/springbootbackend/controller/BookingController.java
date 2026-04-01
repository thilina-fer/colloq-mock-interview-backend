package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.BookingDTO;
import lk.ijse.springbootbackend.entity.BookingStatus;
import lk.ijse.springbootbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hire")
    public ResponseEntity<String> hire(@RequestBody BookingDTO dto) {
        try {
            String response = bookingService.hireInterviewer(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<String> approveBooking(@PathVariable("bookingId") Long bookingId) { // 🎯 ("bookingId") කියලා පැහැදිලිවම දාන්න
        try {
//            System.out.println("🚀 [Backend] Approving Booking ID: " + bookingId);
            String result = bookingService.updateBookingStatus(bookingId, BookingStatus.APPROVED);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
//            System.err.println(" [Backend] Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/candidate/my-bookings")
    public ResponseEntity<List<BookingDTO>> getCandidateBookings(Principal principal) {
        List<BookingDTO> myBookings = bookingService.getBookingsForCandidate(principal.getName());
        return ResponseEntity.ok(myBookings);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.rejectBooking(id));
    }

    @GetMapping("/interviewer/my-requests")
    public ResponseEntity<List<BookingDTO>> getForInterviewer(Principal principal) {
        // principal.getName() එකෙන් ලොග් වෙලා ඉන්න Interviewer ගේ Username එක Service එකට යනවා
        return ResponseEntity.ok(bookingService.getBookingsByInterviewer(principal.getName()));
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<List<BookingDTO>> getForCandidate(@PathVariable("id") Long id) { // 🎯 මෙතන ("id") කියලා අනිවාර්යයෙන්ම දාන්න
        return ResponseEntity.ok(bookingService.getBookingsByCandidate(id));
    }

    @GetMapping("/interviewer/approved")
    public ResponseEntity<List<BookingDTO>> getApprovedRequests(Principal principal) {
        return ResponseEntity.ok(bookingService.getApprovedBookingsForInterviewer(principal.getName()));
    }
}