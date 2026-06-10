package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @Size(max = 100, message = "Transaction id must not exceed 100 characters")
    private String transactionId;

    @NotNull(message = "Booking id is required")
    @Positive(message = "Booking id must be a positive number")
    private Integer bookingId;
}