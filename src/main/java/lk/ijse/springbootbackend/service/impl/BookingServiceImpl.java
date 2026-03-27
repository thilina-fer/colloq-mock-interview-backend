package lk.ijse.springbootbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lk.ijse.springbootbackend.dto.BookingDTO;
import lk.ijse.springbootbackend.entity.*;
import lk.ijse.springbootbackend.repo.*;
import lk.ijse.springbootbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class BookingServiceImpl implements BookingService {


    private final BookingRepo bookingRepo;
    private final CandidateRepo candidateRepo;
    private final InterviewerRepo interviewerRepo;
    private final InterviewerAvailabilityRepo availabilityRepo;
    private final LevelRepo levelRepo;
    private final ModelMapper modelMapper;

    @Override
    public String hireInterviewer(BookingDTO dto) {
        InterviewerAvailability availability = availabilityRepo.findById(dto.getAvailabilityId())
                .orElseThrow(() -> new EntityNotFoundException("Availability slot not found with ID: " + dto.getAvailabilityId()));

        if (availability.isBooked()) {
            throw new IllegalStateException("This time slot is already booked or pending approval.");
        }

        Candidate candidate = candidateRepo.findById(dto.getCandidateId())
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));
        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
                .orElseThrow(() -> new EntityNotFoundException("Interviewer not found"));
        Level level = levelRepo.findById(dto.getLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Level not found"));

        Bookings booking = modelMapper.map(dto, Bookings.class);
        booking.setCandidate(candidate);
        booking.setInterviewer(interviewer);
        booking.setAvailability(availability);
        booking.setLevel(level);
        booking.setStatus(BookingStatus.PENDING_APPROVAL);
        bookingRepo.save(booking);

        availability.setBooked(true);
        availabilityRepo.save(availability);
        return "Hiring request sent successfully! Waiting for interviewer approval.";

    }

    @Override
    public String approveBooking(Long bookingId) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking record not found"));

        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending bookings can be approved.");
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepo.save(booking);
        return "Booking approved. Candidate is notified to proceed with payment.";

    }


    @Override
    public String rejectBooking(Long bookingId) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking record not found"));
        booking.setStatus(BookingStatus.REJECTED);
        InterviewerAvailability availability = booking.getAvailability();
        availability.setBooked(false);
        availabilityRepo.save(availability);
        bookingRepo.save(booking);

        return "Booking rejected and the slot has been released.";
    }


    @Override
    public List<BookingDTO> getBookingsByInterviewer(Long interviewerId) {
        return bookingRepo.findByInterviewer_InterviewerIdAndStatus(interviewerId, BookingStatus.PENDING_APPROVAL)
                .stream()
                .map(b -> modelMapper.map(b, BookingDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDTO> getBookingsByCandidate(Long candidateId) {
        return bookingRepo.findByCandidate_CandidateId(candidateId)
                .stream()
                .map(b -> modelMapper.map(b, BookingDTO.class))
                .collect(Collectors.toList());

    }

}