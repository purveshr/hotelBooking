package com.oyo.hotelBooking.controller;

import com.oyo.hotelBooking.dtos.UserRequestDto;
import com.oyo.hotelBooking.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "Operations related to user management")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Welcome API", description = "Returns greeting message")
    @GetMapping("/welcome")
    public String greet(){
        return "Hello";
    }

    @Operation(summary = "Register User", description = "Creates a new user")
    @PostMapping("/signUp")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDto userRequestDto){

        String massage = userService.registerUser(userRequestDto);
        return ResponseEntity.ok(massage);
    }

    @Operation(summary = "Get User Role", description = "Fetch role by email ID")
    @GetMapping("/role")
    public ResponseEntity<String> getRole
            (
                    @RequestParam
                    @NotBlank(message = "Email is required")
                    @Email(message = "Invalid email format")
                    String emailId)
    {
        String massage = userService.getUserRole(emailId);
        return ResponseEntity.ok(massage);
    }
}
