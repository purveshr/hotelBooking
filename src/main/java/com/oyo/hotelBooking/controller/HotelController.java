
package com.oyo.hotelBooking.controller;

import com.oyo.hotelBooking.dtos.HotelRequestDTO;
import com.oyo.hotelBooking.dtos.HotelResponseDTO;
import com.oyo.hotelBooking.services.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@Tag(name = "Hotel API", description = "Operations related to hotel management")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Operation(summary = "Create a new hotel", description = "Creates a new hotel with the provided details")
    @PostMapping
    @PreAuthorize("hasAnyRole('HOTEL_OWNER','ADMIN')")
    public ResponseEntity<HotelResponseDTO> createHotel(@Valid @RequestBody HotelRequestDTO hotelRequestDTO) {
        HotelResponseDTO hotelResponseDTO = hotelService.createHotel(hotelRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelResponseDTO);
    }

    @Operation(summary = "Create multiple hotels", description = "Creates multiple hotels in a single request")
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('HOTEL_OWNER','ADMIN')")
    public ResponseEntity<List<HotelResponseDTO>> createMultipleHotels(@Valid @RequestBody List<HotelRequestDTO> hotelRequestDTOs) {
        List<HotelResponseDTO> hotelResponseDTOs = hotelService.createMultipleHotels(hotelRequestDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelResponseDTOs);
    }

    @Operation(summary = "Get all hotels", description = "Retrieves a list of all hotels")
    @GetMapping
    public ResponseEntity<List<HotelResponseDTO>> getAllHotels() {
        List<HotelResponseDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @Operation(summary = "Get hotel by ID", description = "Retrieves a specific hotel by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Integer id) {
        HotelResponseDTO hotelResponseDTO = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotelResponseDTO);
    }

    @Operation(summary = "Update hotel", description = "Updates an existing hotel with the provided details")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOTEL_OWNER','ADMIN')")
    public ResponseEntity<HotelResponseDTO> updateHotel(@PathVariable Integer id,
                                                        @Valid @RequestBody HotelRequestDTO hotelRequestDTO) {
        HotelResponseDTO hotelResponseDTO = hotelService.updateHotel(id, hotelRequestDTO);
        return ResponseEntity.ok(hotelResponseDTO);
    }

    @Operation(summary = "Delete hotel", description = "Deletes a hotel by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOTEL_OWNER','ADMIN')")
    public ResponseEntity<String> deleteHotel(@PathVariable Integer id) {
        String message = hotelService.deleteHotel(id);
        return ResponseEntity.ok(message);
    }
}
