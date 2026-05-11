package lk.ijse.springbootbackend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class EvaluationRequestDTO {
    private String userName;
    private String email;
    private String level;
    private String role;
    private List<Map<String, String>> chatHistory; // [{speaker: "user", text: "..."}, ...]
}