package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleDTO {
    @NotBlank
    @Email
    private String emailId;
    @NotNull
    private Roles role;
}
