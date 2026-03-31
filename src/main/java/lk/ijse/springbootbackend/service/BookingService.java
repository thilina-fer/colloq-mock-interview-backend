package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.BookingDTO;
import lk.ijse.springbootbackend.entity.BookingStatus;

import java.util.List;

public interface BookingService {
    String hireInterviewer(BookingDTO dto);
    String updateBookingStatus(Long bookingId, BookingStatus status);
    String rejectBooking(Long bookingId);
    public List<BookingDTO> getBookingsByInterviewer(String username);
    List<BookingDTO> getBookingsByCandidate(Long candidateId);
    List<BookingDTO> getApprovedBookingsForInterviewer(String username);
}