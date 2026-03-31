package lk.ijse.springbootbackend.dto;

import lk.ijse.springbootbackend.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTO {
    private Long bookingId;
    private String jobType;
    private String candidateNote;
    private BookingStatus status;

    private Long availabilityId;
    private Long interviewerId;
    private Long levelId;

    private String interviewerName;
    private String interviewerProfilePic;
    private String date;
    private String startTime;
    private String endTime;
    private String levelName;

    private String candidateName;
    private String candidateProfilePic;
    private String candidateGithub;
    private String candidateLinkedin;

    private boolean paid;
    private String meetingLink;
}