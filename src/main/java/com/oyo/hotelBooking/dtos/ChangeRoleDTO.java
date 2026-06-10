package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String emailId;

    @NotNull(message = "Role is required")
    private Roles role;
}
