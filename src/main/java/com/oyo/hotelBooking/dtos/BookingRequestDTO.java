package com.oyo.hotelBooking.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Total guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 20, message = "Total guests must not exceed 20")
    private Integer totalGuests;

    @NotNull(message = "Customer id is required")
    @Positive(message = "Customer id must be a positive number")
    private Integer customerId;

    @NotNull(message = "Room id is required")
    @Positive(message = "Room id must be a positive number")
    private Integer roomId;
}
