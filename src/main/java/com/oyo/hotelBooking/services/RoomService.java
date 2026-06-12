package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.RoomRequestDTO;
import com.oyo.hotelBooking.dtos.RoomResponseDTO;
import com.oyo.hotelBooking.entity.Hotel;
import com.oyo.hotelBooking.entity.Room;
import com.oyo.hotelBooking.repository.HotelRepository;
import com.oyo.hotelBooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public RoomResponseDTO addRoom(RoomRequestDTO roomRequestDTO) {
        Hotel hotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Hotel with id " + roomRequestDTO.getHotelId() + " not found"));

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
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Hotel with id " + hotelId + " not found"));

        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return rooms.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public RoomResponseDTO updateRoom(Integer id, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Room with id " + id + " not found"));

        Hotel hotel = hotelRepository.findById(roomRequestDTO.getHotelId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Hotel with id " + roomRequestDTO.getHotelId() + " not found"));

        room.setRoomNumber(roomRequestDTO.getRoomNumber());
        room.setRoomType(roomRequestDTO.getRoomType());
        room.setPricePerNight(roomRequestDTO.getPricePerNight());
        room.setCapacity(roomRequestDTO.getCapacity());
        room.setAvailable(roomRequestDTO.getAvailable() != null ? roomRequestDTO.getAvailable() : room.getAvailable());
        room.setHotel(hotel);

        Room updatedRoom = roomRepository.save(room);
        return convertToResponseDTO(updatedRoom);
    }

    public String deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Room with id " + id + " not found"));

        roomRepository.delete(room);
        return "Room with id " + id + " deleted successfully";
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
