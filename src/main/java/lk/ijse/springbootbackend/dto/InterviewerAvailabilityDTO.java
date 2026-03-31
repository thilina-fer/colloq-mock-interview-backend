package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewerAvailabilityDTO {
    private Long availabilityId;

    // 💡 LocalDate සහ LocalTime වෙනුවට String පාවිච්චි කරමු
    // එවිට Backend mapping වලදී null වැටෙන එක වළක්වා ගත හැකියි
    private String date;
    private String startTime;
    private String endTime;

    private boolean isBooked;
    private Long interviewerId;
}