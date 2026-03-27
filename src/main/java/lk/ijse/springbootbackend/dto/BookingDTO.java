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
    private Long candidateId;
    private Long availabilityId;
    private Long interviewerId;
    private Long levelId;
}