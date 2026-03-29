/*
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

}*/


package lk.ijse.springbootbackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lk.ijse.springbootbackend.dto.BookingDTO;
import lk.ijse.springbootbackend.entity.*;
import lk.ijse.springbootbackend.repo.*;
import lk.ijse.springbootbackend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthRepo authRepo;
    private final ModelMapper modelMapper;

    @Override
    public String hireInterviewer(BookingDTO dto) {
        // 1. Availability Slot එක පරීක්ෂා කිරීම
        InterviewerAvailability availability = availabilityRepo.findById(dto.getAvailabilityId())
                .orElseThrow(() -> new EntityNotFoundException("Requested time slot not found."));

        if (availability.isBooked()) {
            throw new IllegalStateException("This time slot is already booked or pending approval.");
        }

        // 2. දැනට ලොග් වී සිටින User (Candidate) ගේ විස්තර Security Context එකෙන් ගැනීම
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User authentication failed."));

        Candidate candidate = candidateRepo.findByAuth(auth)
                .orElseThrow(() -> new EntityNotFoundException("Candidate profile not found for the logged-in user."));

        // 3. අනෙක් අවශ්‍ය Entities සොයා ගැනීම
        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
                .orElseThrow(() -> new EntityNotFoundException("Selected interviewer not found."));

        Level level = levelRepo.findById(dto.getLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Selected interview level not found."));

        // 4. Booking Object එක නිර්මාණය කිරීම සහ දත්ත ඇතුළත් කිරීම
        Bookings booking = new Bookings();
        booking.setJobType(dto.getJobType());
        booking.setCandidateNote(dto.getCandidateNote());
        booking.setStatus(BookingStatus.PENDING_APPROVAL);
        booking.setCandidate(candidate);
        booking.setInterviewer(interviewer);
        booking.setAvailability(availability);
        booking.setLevel(level);

        // 5. Booking එක සේව් කිරීම සහ Slot එක Booked ලෙස ලකුණු කිරීම
        bookingRepo.save(booking);
        availability.setBooked(true);
        availabilityRepo.save(availability);

        return "Hiring request sent successfully! Waiting for " + interviewer.getAuth().getUsername() + "'s approval.";
    }

    @Override
    public String approveBooking(Long bookingId) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking record not found."));

        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Only pending bookings can be approved.");
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepo.save(booking);

        return "Booking approved. Candidate will be notified to proceed.";
    }

    @Override
    public String rejectBooking(Long bookingId) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking record not found."));

        // Status එක REJECTED කරලා, අර අල්ලගත්ත Slot එක ආපහු නිදහස් කරනවා
        booking.setStatus(BookingStatus.REJECTED);

        InterviewerAvailability availability = booking.getAvailability();
        availability.setBooked(false); // Slot එක ආපහු පාවිච්චි කරන්න පුළුවන්

        availabilityRepo.save(availability);
        bookingRepo.save(booking);

        return "Booking rejected and the time slot has been released.";
    }

    @Override
    public List<BookingDTO> getBookingsByInterviewer(Long interviewerId) {
        // Interviewer ට තමන්ගේ Pending Requests විතරක් පෙන්වීමට
        return bookingRepo.findByInterviewer_InterviewerIdAndStatus(interviewerId, BookingStatus.PENDING_APPROVAL)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByCandidate(Long candidateId) {
        // Candidate ට තමන්ගේ සම්පූර්ණ Booking History එකම පෙන්වීමට
        return bookingRepo.findByCandidate_CandidateId(candidateId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 💡 Helper method to convert Booking Entity to DTO with UI details
     */
    private BookingDTO convertToDTO(Bookings booking) {
        BookingDTO dto = modelMapper.map(booking, BookingDTO.class);

        // Frontend එකේ Display කරන්න ඕනේ අමතර දත්ත ටික මෙතනදී Manual සෙට් කරනවා
        dto.setInterviewerName(booking.getInterviewer().getAuth().getUsername());
        dto.setInterviewerProfilePic(booking.getInterviewer().getAuth().getProfilePic());

        if (booking.getAvailability() != null) {
            dto.setDate(booking.getAvailability().getDate().toString());
            dto.setStartTime(booking.getAvailability().getStartTime().toString());
            dto.setEndTime(booking.getAvailability().getEndTime().toString());
        }

        if (booking.getLevel() != null) {
            dto.setLevelName(booking.getLevel().getName());
        }

        return dto;
    }
}