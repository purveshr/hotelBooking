package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequestDTO {

    @NotBlank(message = "Room number is required")
    @Size(min = 1, max = 10, message = "Room number must be between 1 and 10 characters")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price per night must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits and 2 decimal places")
    private BigDecimal pricePerNight;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity must not exceed 100")
    private Integer capacity;

    private Boolean available;

    @NotNull(message = "Hotel id is required")
    @Positive(message = "Hotel id must be a positive number")
    private Integer hotelId;
}