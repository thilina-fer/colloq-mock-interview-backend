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
        // 1. Check Availability Slot
        InterviewerAvailability availability = availabilityRepo.findById(dto.getAvailabilityId())
                .orElseThrow(() -> new EntityNotFoundException("Requested time slot not found."));

        if (availability.isBooked()) {
            throw new IllegalStateException("This time slot is already booked or pending approval.");
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Auth auth = authRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User authentication failed for: " + currentUsername));

        Candidate candidate = candidateRepo.findByAuth(auth)
                .orElseThrow(() -> new EntityNotFoundException("Candidate profile not found for the logged-in user."));

        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
                .orElseThrow(() -> new EntityNotFoundException("Selected interviewer not found."));

        Level level = levelRepo.findById(dto.getLevelId())
                .orElseThrow(() -> new EntityNotFoundException("Selected interview level not found."));

        Bookings booking = new Bookings();
        booking.setJobType(dto.getJobType());
        booking.setCandidateNote(dto.getCandidateNote());
        booking.setStatus(BookingStatus.PENDING_APPROVAL);
        booking.setCandidate(candidate);
        booking.setInterviewer(interviewer);
        booking.setAvailability(availability);
        booking.setLevel(level);

        bookingRepo.save(booking);
        availability.setBooked(true);
        availabilityRepo.save(availability);

        return "Hiring request sent successfully! Waiting for " + interviewer.getAuth().getUsername() + "'s approval.";
    }

//    @Override
//    public String approveBooking(Long bookingId) {
//        Bookings booking = bookingRepo.findById(bookingId)
//                .orElseThrow(() -> new EntityNotFoundException("Booking record not found."));
//
//        if (booking.getStatus() != BookingStatus.PENDING_APPROVAL) {
//            throw new IllegalStateException("Only pending bookings can be approved.");
//        }
//
//        booking.setStatus(BookingStatus.APPROVED);
//        bookingRepo.save(booking);
//
//        return "Booking approved. Candidate will be notified to proceed.";
//    }

    @Override
    public String rejectBooking(Long bookingId) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking record not found."));

        booking.setStatus(BookingStatus.REJECTED);

        InterviewerAvailability availability = booking.getAvailability();
        availability.setBooked(false);

        availabilityRepo.save(availability);
        bookingRepo.save(booking);

        return "Booking rejected and the time slot has been released.";
    }

    @Override
    public List<BookingDTO> getBookingsByInterviewer(String username) {

        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        return bookingRepo.findByInterviewer_InterviewerIdAndStatus(
                        interviewer.getInterviewerId(),
                        BookingStatus.PENDING_APPROVAL
                )
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // BookingServiceImpl.java
    @Override
    public List<BookingDTO> getBookingsByCandidate(Long candidateId) {
        return bookingRepo.findByCandidate_CandidateId(candidateId) // 🎯 මෙතන query එක check කරන්න
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updateBookingStatus(Long bookingId, BookingStatus status) {
        Bookings booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);

        if (status == BookingStatus.APPROVED) {
            if (booking.getAvailability() != null) {
                // 🎯 මෙතන setter එක නිවැරදිව පාවිච්චි කරන්න
                booking.getAvailability().setBooked(true);
                availabilityRepo.save(booking.getAvailability());
            }
        }

        bookingRepo.save(booking);
        return "Status updated to " + status;
    }
    private BookingDTO convertToDTO(Bookings booking) {
        BookingDTO dto = modelMapper.map(booking, BookingDTO.class);

        // 1. Interviewer Details (කලින් තිබුණු ඒවා)
        if (booking.getInterviewer() != null && booking.getInterviewer().getAuth() != null) {
            dto.setInterviewerName(booking.getInterviewer().getAuth().getUsername());
            dto.setInterviewerProfilePic(booking.getInterviewer().getAuth().getProfilePic());
        }

        // 2. [FIX]: Candidate Details (මෙන්න මේ ටික තමයි Card එකට ඕනේ)
        if (booking.getCandidate() != null && booking.getCandidate().getAuth() != null) {
            // Candidate ගේ නම සහ රූපය එන්නේ Auth table එකෙන්
            dto.setCandidateName(booking.getCandidate().getAuth().getUsername());
            dto.setCandidateProfilePic(booking.getCandidate().getAuth().getProfilePic());

            // Github සහ Linkedin එන්නේ Candidate table එකෙන් (ඒවා entity එකේ තියෙන විදිහට field names මාරු කරන්න)
            dto.setCandidateGithub(booking.getCandidate().getGithubUrl());
            dto.setCandidateLinkedin(booking.getCandidate().getLinkedinUrl());
        }

        // 3. Availability Details
        if (booking.getAvailability() != null) {
            dto.setDate(booking.getAvailability().getDate().toString());
            dto.setStartTime(booking.getAvailability().getStartTime().toString());
            dto.setEndTime(booking.getAvailability().getEndTime().toString());
        }

        // 4. Level Details
        if (booking.getLevel() != null) {
            dto.setLevelName(booking.getLevel().getName());
        }

        return dto;
    }
}