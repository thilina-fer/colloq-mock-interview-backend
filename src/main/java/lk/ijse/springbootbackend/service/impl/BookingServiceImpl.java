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

//    @Override
//    public String hireInterviewer(BookingDTO dto) {
//        InterviewerAvailability availability = availabilityRepo.findById(dto.getAvailabilityId())
//                .orElseThrow(() -> {
//                    return new EntityNotFoundException("Requested time slot not found.");
//                });
//
//        if (availability.isBooked()) {
//            throw new IllegalStateException("This time slot is already booked or pending approval.");
//        }
//
//        // 2. දැනට ලොග් වී සිටින User (Candidate) ගේ විස්තර Security Context එකෙන් ගැනීම
//        String currentUsername  = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println("🔍 [Auth Check] Current User Email from SecurityContext: [" + currentUsername + "]");
//
//        Auth auth = authRepo.findByUsername(currentUsername)
//                .orElseThrow(() -> {
//                    System.out.println("❌ ERROR: Auth record NOT FOUND in DB for email: [" + currentUsername + "]");
//                    return new EntityNotFoundException("User authentication failed.");
//                });
//        System.out.println("✅ [Step 2] Auth record found: ID " + auth.getAuthId() + ", Role: " + auth.getRole());
//
//        Candidate candidate = candidateRepo.findByAuth(auth)
//                .orElseThrow(() -> {
//                    System.out.println("❌ ERROR: Candidate profile NOT FOUND for Auth ID: " + auth.getAuthId());
//                    return new EntityNotFoundException("Candidate profile not found for the logged-in user.");
//                });
//        System.out.println("✅ [Step 2.1] Candidate profile found: ID " + candidate.getCandidateId());
//
//        // 3. අනෙක් අවශ්‍ය Entities සොයා ගැනීම
//        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
//                .orElseThrow(() -> {
//                    System.out.println("❌ ERROR: Interviewer NOT FOUND for ID: " + dto.getInterviewerId());
//                    return new EntityNotFoundException("Selected interviewer not found.");
//                });
//        System.out.println("✅ [Step 3] Interviewer found: " + interviewer.getAuth().getUsername());
//
//        Level level = levelRepo.findById(dto.getLevelId())
//                .orElseThrow(() -> {
//                    System.out.println("❌ ERROR: Level NOT FOUND for ID: " + dto.getLevelId());
//                    return new EntityNotFoundException("Selected interview level not found.");
//                });
//        System.out.println("✅ [Step 3.1] Level found: " + level.getName());
//
//        // 4. Booking Object එක නිර්මාණය කිරීම සහ දත්ත ඇතුළත් කිරීම
//        System.out.println("📝 [Step 4] Creating new Booking object...");
//        Bookings booking = new Bookings();
//        booking.setJobType(dto.getJobType());
//        booking.setCandidateNote(dto.getCandidateNote());
//        booking.setStatus(BookingStatus.PENDING_APPROVAL);
//        booking.setCandidate(candidate);
//        booking.setInterviewer(interviewer);
//        booking.setAvailability(availability);
//        booking.setLevel(level);
//
//        // 5. Booking එක සේව් කිරීම සහ Slot එක Booked ලෙස ලකුණු කිරීම
//        System.out.println("💾 [Step 5] Saving booking and updating slot status...");
//        bookingRepo.save(booking);
//        availability.setBooked(true);
//        availabilityRepo.save(availability);
//
//        System.out.println("🎉 [Success] Booking saved successfully for candidate: " + candidate.getAuth().getEmail());
//
//        return "Hiring request sent successfully! Waiting for " + interviewer.getAuth().getUsername() + "'s approval.";
//    }

    @Override
    public String hireInterviewer(BookingDTO dto) {
        // 1. Availability Slot එක පරීක්ෂා කිරීම
        InterviewerAvailability availability = availabilityRepo.findById(dto.getAvailabilityId())
                .orElseThrow(() -> new EntityNotFoundException("Requested time slot not found."));

        if (availability.isBooked()) {
            throw new IllegalStateException("This time slot is already booked or pending approval.");
        }

        // 2. දැනට ලොග් වී සිටින User (Candidate) ගේ විස්තර Security Context එකෙන් ගැනීම
        // 🎯 [Note] අපි හොයාගත්ත විදිහට මෙතනින් එන්නේ Username එකයි
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        Auth auth = authRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User authentication failed for: " + currentUsername));

        Candidate candidate = candidateRepo.findByAuth(auth)
                .orElseThrow(() -> new EntityNotFoundException("Candidate profile not found for the logged-in user."));

        // 3. අනෙක් අවශ්‍ය Entities සොයා ගැනීම (Interviewer සහ Level)
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
    public List<BookingDTO> getBookingsByInterviewer(String username) { // 🎯 Long id වෙනුවට String username ගත්තා

        // 1. [FIX]: Token එකෙන් එන Username එක හරහා Interviewer ව හොයාගන්න
        Auth auth = authRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        Interviewer interviewer = interviewerRepo.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Interviewer profile not found"));

        // 2. දැන් අදාළ Interviewer ID එක සහ PENDING_APPROVAL status එකට filter කරනවා
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