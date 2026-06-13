package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.RoomRequestDTO;
import com.oyo.hotelBooking.dtos.RoomResponseDTO;
import com.oyo.hotelBooking.entity.Hotel;
import com.oyo.hotelBooking.entity.Room;
import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.enums.Roles;
import com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException;
import com.oyo.hotelBooking.repository.HotelRepository;
import com.oyo.hotelBooking.repository.RoomRepository;
import com.oyo.hotelBooking.security.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final CurrentUserService currentUserService;

    public RoomService(RoomRepository roomRepository,
                       HotelRepository hotelRepository,
                       CurrentUserService currentUserService) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.currentUserService = currentUserService;
    }

    public RoomResponseDTO addRoom(RoomRequestDTO roomRequestDTO) {
        Hotel hotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel with id " + roomRequestDTO.getHotelId() + " not found"
                ));

        validateHotelOwnership(hotel);

        Room room = Room.builder()
                .roomNumber(roomRequestDTO.getRoomNumber())
                .roomType(roomRequestDTO.getRoomType())
                .pricePerNight(roomRequestDTO.getPricePerNight())
                .capacity(roomRequestDTO.getCapacity())
                .available(roomRequestDTO.getAvailable() != null ? roomRequestDTO.getAvailable() : true)
                .hotel(hotel)
                .build();

        Room savedRoom = roomRepository.save(room);
        return convertToResponseDTO(savedRoom);
    }

    public List<RoomResponseDTO> listRoomsByHotel(Integer hotelId) {
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + hotelId + " not found"));

        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public RoomResponseDTO updateRoom(Integer id, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " not found"));

        validateHotelOwnership(room.getHotel());

        Hotel requestedHotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel with id " + roomRequestDTO.getHotelId() + " not found"
                ));

        validateHotelOwnership(requestedHotel);

        room.setRoomNumber(roomRequestDTO.getRoomNumber());
        room.setRoomType(roomRequestDTO.getRoomType());
        room.setPricePerNight(roomRequestDTO.getPricePerNight());
        room.setCapacity(roomRequestDTO.getCapacity());
        room.setAvailable(roomRequestDTO.getAvailable() != null ? roomRequestDTO.getAvailable() : room.getAvailable());
        room.setHotel(requestedHotel);

        Room updatedRoom = roomRepository.save(room);
        return convertToResponseDTO(updatedRoom);
    }

    public String deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " not found"));

        validateHotelOwnership(room.getHotel());

        roomRepository.delete(room);
        return "Room with id " + id + " deleted successfully";
    }

    private void validateHotelOwnership(Hotel hotel) {
        User current = currentUserService.getCurrentUser();

        if (current.getRole() == Roles.ADMIN) {
            return;
        }

        if (current.getRole() != Roles.HOTEL_OWNER) {
            throw new AccessDeniedException("Only ADMIN or HOTEL_OWNER can manage rooms");
        }

        if (hotel == null || hotel.getOwner() == null || !hotel.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("You are not allowed to manage rooms for this hotel");
        }
    }

    private RoomResponseDTO convertToResponseDTO(Room room) {
        RoomResponseDTO responseDTO = new RoomResponseDTO();
        responseDTO.setId(room.getId());
        responseDTO.setRoomNumber(room.getRoomNumber());
        responseDTO.setRoomType(room.getRoomType());
        responseDTO.setPricePerNight(room.getPricePerNight());
        responseDTO.setCapacity(room.getCapacity());
        responseDTO.setAvailable(room.getAvailable());
        responseDTO.setHotelId(room.getHotel().getId());
        responseDTO.setHotelName(room.getHotel().getName());
        return responseDTO;
    }
}