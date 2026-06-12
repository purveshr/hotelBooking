package com.oyo.hotelBooking.controller;

import com.oyo.hotelBooking.dtos.BookingRequestDTO;
import com.oyo.hotelBooking.dtos.BookingResponseDTO;
import com.oyo.hotelBooking.dtos.BookingStatusUpdateDTO;
import com.oyo.hotelBooking.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Booking API", description = "Operations related to booking management")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Create a booking", description = "Creates a booking for a room")
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        BookingResponseDTO response = bookingService.createBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get booking by id", description = "Get booking details by id")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @Operation(summary = "List bookings for a user", description = "List all bookings for a given user/customer")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> listByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(bookingService.listBookingsByUser(userId));
    }

    @Operation(summary = "Update booking status", description = "Update status of an existing booking")
    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateStatus(@PathVariable Integer id,
                                                           @Valid @RequestBody BookingStatusUpdateDTO dto) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, dto));
    }
}

