
package com.oyo.hotelBooking.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalTime;
import com.oyo.hotelBooking.validation.ValidHotelTimes;

@Data
@ValidHotelTimes
public class HotelRequestDTO {

    @NotBlank(message = "Hotel name is required")
    @Size(min = 2, max = 100, message = "Hotel name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
    private String city;

    @Size(max = 50, message = "State name must not exceed 50 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 50, message = "Country name must be between 2 and 50 characters")
    private String country;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Owner id is required")
    @Positive(message = "Owner id must be a positive number")
    private Integer ownerId;

    // Optional: if not provided, defaults will be used (15:00 check-in, 11:00 check-out)
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
}
