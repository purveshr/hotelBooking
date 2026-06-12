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
@Constraint(validatedBy = ValidHotelTimesValidator.class)
@Documented
public @interface ValidHotelTimes {
    String message() default "Hotel check-in time must be before check-out time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

