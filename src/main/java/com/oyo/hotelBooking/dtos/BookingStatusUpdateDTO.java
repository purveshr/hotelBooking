package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdateDTO {

    @NotNull(message = "Booking status is required")
    private BookingStatus bookingStatus;
}
