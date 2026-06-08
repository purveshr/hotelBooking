package com.oyo.hotelBooking.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @NotNull(message = "Total guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    private Integer totalGuests;

    @NotNull(message = "Customer id is required")
    private Integer customerId;

    @NotNull(message = "Room id is required")
    private Integer roomId;
}
