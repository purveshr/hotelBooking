package com.oyo.hotelBooking.validation;

import com.oyo.hotelBooking.dtos.HotelRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;

public class ValidHotelTimesValidator implements ConstraintValidator<ValidHotelTimes, HotelRequestDTO> {

    @Override
    public boolean isValid(HotelRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return true;
        LocalTime in = dto.getCheckInTime();
        LocalTime out = dto.getCheckOutTime();
        if (in == null || out == null) return true; // allow defaults elsewhere
        return in.isBefore(out);
    }
}

