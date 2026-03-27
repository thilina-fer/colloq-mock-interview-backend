package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.BookingDTO;
import lk.ijse.springbootbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hire")
    public ResponseEntity<String> hire(@RequestBody BookingDTO dto) {
        return new ResponseEntity<>(bookingService.hireInterviewer(dto), HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.approveBooking(id));
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.rejectBooking(id));
    }

    @GetMapping("/interviewer/{id}")
    public ResponseEntity<List<BookingDTO>> getForInterviewer(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingsByInterviewer(id));
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<List<BookingDTO>> getForCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingsByCandidate(id));
    }
}