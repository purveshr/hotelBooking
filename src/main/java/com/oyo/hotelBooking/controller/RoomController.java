package com.oyo.hotelBooking.controller;

import com.oyo.hotelBooking.dtos.RoomRequestDTO;
import com.oyo.hotelBooking.dtos.RoomResponseDTO;
import com.oyo.hotelBooking.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@Tag(name = "Room API", description = "Operations related to room management")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Add a new room", description = "Creates a new room with the provided details")
    @PostMapping
    public ResponseEntity<RoomResponseDTO> addRoom(@Valid @RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO roomResponseDTO = roomService.addRoom(roomRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomResponseDTO);
    }

    @Operation(summary = "List rooms by hotel", description = "Retrieves a list of all rooms for a specific hotel")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> listRoomsByHotel(@PathVariable Integer hotelId) {
        List<RoomResponseDTO> rooms = roomService.listRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Update room", description = "Updates an existing room with the provided details")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Integer id,
                                                      @Valid @RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO roomResponseDTO = roomService.updateRoom(id, roomRequestDTO);
        return ResponseEntity.ok(roomResponseDTO);
    }

    @Operation(summary = "Delete room", description = "Deletes a room by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Integer id) {
        String message = roomService.deleteRoom(id);
        return ResponseEntity.ok(message);
    }
}

