
package com.oyo.hotelBooking.controller;

import com.oyo.hotelBooking.dtos.ChangeRoleDTO;
import com.oyo.hotelBooking.dtos.LoginRequestDTO;
import com.oyo.hotelBooking.dtos.LoginResponseDTO;
import com.oyo.hotelBooking.dtos.UserRequestDTO;
import com.oyo.hotelBooking.enums.Roles;
import com.oyo.hotelBooking.security.JwtUtil;
import com.oyo.hotelBooking.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "Operations related to user management")
@Validated
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Welcome API", description = "Returns greeting message")
    @GetMapping("/welcome")
    public String greet() {
        return "Hello";
    }

    @Operation(summary = "Register User", description = "Creates a new user")
    @PostMapping("/signUp")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDTO userRequestDto) {
        String message = userService.registerUser(userRequestDto);
        return ResponseEntity.ok(message);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get User Role", description = "Fetch role by email ID")
    @GetMapping("/role")
    public ResponseEntity<Roles> getRole(
            @RequestParam
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String emailId) {

        Roles message = userService.getUserRole(emailId);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Login User", description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailId(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getEmailId());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setMessage("Login successful");

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Change user role", description = "Requires ADMIN role to change users role, roles can be changed toHOTEL_OWNER, CUSTOMER, ADMIN")
    @PutMapping("/changeUserRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserRole(@Valid @RequestBody ChangeRoleDTO dto) {
        Roles updatedRole = userService.changeRole(dto);
        return ResponseEntity.ok("User role updated to: " + updatedRole);
    }

}
