/*
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
}*/


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
@CrossOrigin // Frontend එකත් එක්ක Connect වෙන්න අනිවාර්යයි
public class BookingController {

    private final BookingService bookingService;

    /**
     * 🚀 Candidate කෙනෙක් Interviewer කෙනෙක්ව Hire කරන Endpoint එක
     * Payload: { availabilityId, interviewerId, levelId, jobType, candidateNote }
     */
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

    /**
     * ✅ Interviewer විසින් Booking එකක් Approve කිරීම
     */
    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.approveBooking(id));
    }

    /**
     * ❌ Interviewer විසින් Booking එකක් Reject කිරීම
     */
    @PutMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.rejectBooking(id));
    }

    /**
     * 📊 Interviewer ට තමන්ට ලැබුණු Pending Requests බලාගැනීමට
     */
    @GetMapping("/interviewer/{id}")
    public ResponseEntity<List<BookingDTO>> getForInterviewer(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingsByInterviewer(id));
    }

    /**
     * 📋 Candidate ට තමන්ගේ Booking History එක (Dashboard එකේ පෙන්වන්න) බලාගැනීමට
     */
    @GetMapping("/candidate/{id}")
    public ResponseEntity<List<BookingDTO>> getForCandidate(@PathVariable("id") Long id) { // 🎯 මෙතන ("id") කියලා අනිවාර්යයෙන්ම දාන්න
        return ResponseEntity.ok(bookingService.getBookingsByCandidate(id));
    }
}