package lk.ijse.springbootbackend.dto;

import lombok.Data;

@Data
public class PaymentDto {
    private Double amount;
    private String paymentMethod;
    private String transactionId;
    private Long bookingId;
}