package com.oyo.hotelBooking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidBookingDatesValidator.class)
@Documented
public @interface ValidBookingDates {
    String message() default "Check-out date must be after check-in date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

