package lk.ijse.springbootbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class APIResponse {
    private int statusCode;
    private String message;
    private Object data;
}
