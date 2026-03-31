//package lk.ijse.springbootbackend.controller;
//
//import lk.ijse.springbootbackend.dto.APIResponse;
//import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
//import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
//import lk.ijse.springbootbackend.service.InterviewerService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/interviewer")
//@RequiredArgsConstructor
//@CrossOrigin("http://localhost:5173")
//public class InterviewerController {
//    private final InterviewerService interviewerService;
//
//   /* @PostMapping("/complete-interviewer-profile")
//    public String completeProfile(@RequestBody CompleteInterviewerProfileDTO completeInterviewerProfileDTO , Authentication authentication) {
//        return interviewerService.completeInterviewerProfile(completeInterviewerProfileDTO , authentication.getName());
//    }*/
//
//    @PostMapping(value = "/complete-interviewer-profile", consumes = {"multipart/form-data"})
//    public ResponseEntity<APIResponse> completeProfile(
//            @RequestPart("data") CompleteInterviewerProfileDTO dto,
//            @RequestPart(value = "image", required = false) MultipartFile image,
//            Authentication authentication) {
//
//        // Service එකට image එකත් pass කරන්න (ඔයාගේ service එකේ මේක update කරගන්න)
//        String result = interviewerService.completeInterviewerProfile(dto, image, authentication.getName());
//        return ResponseEntity.ok(new APIResponse(200, "Success", result));
//    }
//
//    // READ - own profile
//    @GetMapping("/profile")
//    public ResponseEntity<APIResponse> getProfile(Authentication authentication) {
//        InterviewerResponseDTO data = interviewerService.getInterviewerProfile(authentication.getName());
//        return ResponseEntity.ok(new APIResponse(200, "Success", data));
//    }
//
//    // UPDATE
////    @PutMapping("/update-profile")
////    public ResponseEntity<APIResponse> updateProfile(
////            @RequestBody CompleteInterviewerProfileDTO dto,
////            Authentication authentication) {
////        String msg = interviewerService.updateInterviewerProfile(dto, authentication.getName());
////        return ResponseEntity.ok(new APIResponse(200, msg, null));
////    }
//
//    // InterviewerController.java
//
//    @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<APIResponse> updateProfile(
//            @RequestPart("data") CompleteInterviewerProfileDTO dto,
//            @RequestPart(value = "image", required = false) MultipartFile image,
//            Authentication authentication) {
//
//        // Service එකට data සහ image එක යවනවා
//        String result = interviewerService.updateInterviewerProfile(dto, image, authentication.getName());
//        return ResponseEntity.ok(new APIResponse(200, "Profile updated successfully", result));
//    }
//
//    // DELETE
//    @DeleteMapping("/delete-profile/{interviewerId}")
//    public ResponseEntity<APIResponse> deleteProfile(
//            @PathVariable Long interviewerId,
//            Authentication authentication) {
//        String msg = interviewerService.deleteInterviewerProfile(interviewerId, authentication.getName());
//        return ResponseEntity.ok(new APIResponse(200, msg, null));
//    }
//    @GetMapping("/all")
//    public ResponseEntity<List<InterviewerResponseDTO>> getAllInterviewers() {
//        // 🎯 Service එකේදී අපි price එක set කරන නිසා මෙතනින් එන JSON එකේ price එක තියෙයි
//        return ResponseEntity.ok(interviewerService.getAllInterviewers());
//    }
//
//
//}


package lk.ijse.springbootbackend.controller;

import lk.ijse.springbootbackend.dto.APIResponse;
import lk.ijse.springbootbackend.dto.auth.CompleteInterviewerProfileDTO;
import lk.ijse.springbootbackend.dto.InterviewerResponseDTO;
import lk.ijse.springbootbackend.service.InterviewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviewer")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class InterviewerController {
    private final InterviewerService interviewerService;

    // COMPLETE PROFILE - Level selection සහ Image upload එකත් එක්ක
    @PostMapping(value = "/complete-interviewer-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse> completeProfile(
            @RequestPart("data") CompleteInterviewerProfileDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {

        // Service එකේදී දැන් dto.levelId සහ image එක handle වෙනවා
        String result = interviewerService.completeInterviewerProfile(dto, image, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Success", result));
    }

    // READ OWN PROFILE
    @GetMapping("/profile")
    public ResponseEntity<APIResponse> getProfile(Authentication authentication) {
        InterviewerResponseDTO data = interviewerService.getInterviewerProfile(authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Success", data));
    }

    // UPDATE PROFILE
    @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse> updateProfile(
            @RequestPart("data") CompleteInterviewerProfileDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {

        String result = interviewerService.updateInterviewerProfile(dto, image, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Profile updated successfully", result));
    }

    // DELETE PROFILE
    @DeleteMapping("/delete-profile/{interviewerId}")
    public ResponseEntity<APIResponse> deleteProfile(
            @PathVariable Long interviewerId,
            Authentication authentication) {
        String msg = interviewerService.deleteInterviewerProfile(interviewerId, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, msg, null));
    }

    // 🎯 ලොග් වුණු ඕනෑම කෙනෙක්ට ACTIVE experts ලා බලන්න (Modal එක සඳහා)
    @GetMapping("/all")
    public ResponseEntity<APIResponse> getActiveInterviewers() {
        List<InterviewerResponseDTO> interviewers = interviewerService.getActiveInterviewers();
        return ResponseEntity.ok(new APIResponse(200, "Success", interviewers));
    }

    // 🎯 Admin ට විතරක් හැමෝවම පාලනය කරන්න
    @GetMapping("/admin/all")
    public ResponseEntity<APIResponse> getAllForAdmin() {
        List<InterviewerResponseDTO> interviewers = interviewerService.getAllInterviewersForAdmin();
        return ResponseEntity.ok(new APIResponse(200, "Success", interviewers));
    }

//
//    // GET ALL INTERVIEWERS - Candidate Dashboard එකට
//    @GetMapping("/all")
//    public ResponseEntity<List<InterviewerResponseDTO>> getAllInterviewers() {
//        // 🎯 Service එකේදී අපි price සහ levelName set කරන නිසා මෙතනින් එන JSON එකේ ඒ දත්ත තියෙයි
//        return ResponseEntity.ok(interviewerService.getAllInterviewers());
//    }



}