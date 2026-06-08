package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    private String transactionId;

    @NotNull(message = "Booking id is required")
    private Integer bookingId;
}