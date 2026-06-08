package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentStatusUpdateDTO {

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
}
