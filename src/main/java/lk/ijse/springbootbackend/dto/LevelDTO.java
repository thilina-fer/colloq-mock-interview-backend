package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelDTO {

    private Long levelId;
    private String name;
    private Integer sessionDuration;
    private Double price;
    private String status;
}
