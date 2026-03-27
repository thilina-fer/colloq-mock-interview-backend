package lk.ijse.springbootbackend.service;

import lk.ijse.springbootbackend.dto.PaymentDto;

public interface PaymentService {
    String processPayment(PaymentDto paymentDto);
}