package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.BookingDTO;
import java.util.List;

public interface BookingService {
    String hireInterviewer(BookingDTO dto); // මුලින්ම Hire කරන අවස්ථාව
    String approveBooking(Long bookingId);  // Interviewer Approve කරන අවස්ථාව
    String rejectBooking(Long bookingId);   // Interviewer Reject කරන අවස්ථාව
    List<BookingDTO> getBookingsByInterviewer(Long interviewerId);
    List<BookingDTO> getBookingsByCandidate(Long candidateId);
}