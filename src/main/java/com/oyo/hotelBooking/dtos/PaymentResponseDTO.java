package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {

    private Integer id;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;

    private Integer bookingId;
    private String bookingCode;
}
