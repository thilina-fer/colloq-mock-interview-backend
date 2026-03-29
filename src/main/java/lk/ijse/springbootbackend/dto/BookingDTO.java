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

    // IDs (Request එක එවද්දී ඕනේ වෙනවා)
    private Long availabilityId;
    private Long interviewerId;
    private Long levelId;

    // UI Display Fields (Response එකේදී Frontend එකට පෙන්වන්න)
    private String interviewerName;
    private String interviewerProfilePic;
    private String date;         // Availability එකෙන් එන දවස
    private String startTime;    // Availability එකෙන් එන වෙලාව
    private String endTime;
    private String levelName;    // "Junior", "Senior" වගේ
}