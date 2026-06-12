package com.oyo.hotelBooking.validation;

import com.oyo.hotelBooking.dtos.BookingRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ValidBookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingRequestDTO> {

    @Override
    public boolean isValid(BookingRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return true;
        LocalDate in = dto.getCheckInDate();
        LocalDate out = dto.getCheckOutDate();
        if (in == null || out == null) return true; // other validators will catch nulls
        return out.isAfter(in);
    }
}

