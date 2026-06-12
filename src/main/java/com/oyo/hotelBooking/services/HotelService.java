package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.HotelRequestDTO;
import com.oyo.hotelBooking.dtos.HotelResponseDTO;
import com.oyo.hotelBooking.entity.Hotel;
import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.repository.HotelRepository;
import com.oyo.hotelBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;

@Service
@Transactional
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.oyo.hotelBooking.security.CurrentUserService currentUserService;

    public HotelResponseDTO createHotel(HotelRequestDTO hotelRequestDTO) {
        User owner = userRepository.findById(hotelRequestDTO.getOwnerId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Owner with id " + hotelRequestDTO.getOwnerId() + " not found"));

        Hotel hotel = Hotel.builder()
                .name(hotelRequestDTO.getName())
                .address(hotelRequestDTO.getAddress())
                .city(hotelRequestDTO.getCity())
                .state(hotelRequestDTO.getState())
                .country(hotelRequestDTO.getCountry())
                .description(hotelRequestDTO.getDescription())
                .checkInTime(hotelRequestDTO.getCheckInTime() != null ? hotelRequestDTO.getCheckInTime() : LocalTime.of(15,0))
                .checkOutTime(hotelRequestDTO.getCheckOutTime() != null ? hotelRequestDTO.getCheckOutTime() : LocalTime.of(11,0))
                .owner(owner)
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToResponseDTO(savedHotel);
    }

    public List<HotelResponseDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public HotelResponseDTO getHotelById(Integer id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Hotel with id " + id + " not found"));
        return convertToResponseDTO(hotel);
    }

    public HotelResponseDTO updateHotel(Integer id, HotelRequestDTO hotelRequestDTO) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Hotel with id " + id + " not found"));

        // Authorization: only hotel owner or admin can update the hotel
        com.oyo.hotelBooking.entity.User current = currentUserService.getCurrentUser();
        if (current.getRole() != com.oyo.hotelBooking.enums.Roles.ADMIN && !hotel.getOwner().getId().equals(current.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to update this hotel");
        }

        User owner = userRepository.findById(hotelRequestDTO.getOwnerId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Owner with id " + hotelRequestDTO.getOwnerId() + " not found"));

        hotel.setName(hotelRequestDTO.getName());
        hotel.setAddress(hotelRequestDTO.getAddress());
        hotel.setCity(hotelRequestDTO.getCity());
        hotel.setState(hotelRequestDTO.getState());
        hotel.setCountry(hotelRequestDTO.getCountry());
        hotel.setDescription(hotelRequestDTO.getDescription());
        hotel.setCheckInTime(hotelRequestDTO.getCheckInTime() != null ? hotelRequestDTO.getCheckInTime() : LocalTime.of(15,0));
        hotel.setCheckOutTime(hotelRequestDTO.getCheckOutTime() != null ? hotelRequestDTO.getCheckOutTime() : LocalTime.of(11,0));
        hotel.setOwner(owner);

        Hotel updatedHotel = hotelRepository.save(hotel);
        return convertToResponseDTO(updatedHotel);
    }

    public String deleteHotel(Integer id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Hotel with id " + id + " not found"));

        hotelRepository.delete(hotel);
        return "Hotel with id " + id + " deleted successfully";
    }

    public List<HotelResponseDTO> createMultipleHotels(List<HotelRequestDTO> hotelRequestDTOs) {
        List<Hotel> hotels = hotelRequestDTOs.stream().map(hotelRequestDTO -> {
            User owner = userRepository.findById(hotelRequestDTO.getOwnerId())
                    .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Owner with id " + hotelRequestDTO.getOwnerId() + " not found"));

            return Hotel.builder()
                    .name(hotelRequestDTO.getName())
                    .address(hotelRequestDTO.getAddress())
                    .city(hotelRequestDTO.getCity())
                    .state(hotelRequestDTO.getState())
                    .country(hotelRequestDTO.getCountry())
                    .description(hotelRequestDTO.getDescription())
                    .checkInTime(hotelRequestDTO.getCheckInTime() != null ? hotelRequestDTO.getCheckInTime() : LocalTime.of(15,0))
                    .checkOutTime(hotelRequestDTO.getCheckOutTime() != null ? hotelRequestDTO.getCheckOutTime() : LocalTime.of(11,0))
                    .owner(owner)
                    .build();
        }).collect(Collectors.toList());

        List<Hotel> savedHotels = hotelRepository.saveAll(hotels);
        return savedHotels.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private HotelResponseDTO convertToResponseDTO(Hotel hotel) {
        HotelResponseDTO responseDTO = new HotelResponseDTO();
        responseDTO.setId(hotel.getId());
        responseDTO.setName(hotel.getName());
        responseDTO.setAddress(hotel.getAddress());
        responseDTO.setCity(hotel.getCity());
        responseDTO.setState(hotel.getState());
        responseDTO.setCountry(hotel.getCountry());
        responseDTO.setDescription(hotel.getDescription());
        responseDTO.setStatus(hotel.getStatus());
        responseDTO.setOwnerId(hotel.getOwner().getId());
        responseDTO.setOwnerName(hotel.getOwner().getFirstName() + " " + hotel.getOwner().getLastName());
        responseDTO.setCheckInTime(hotel.getCheckInTime());
        responseDTO.setCheckOutTime(hotel.getCheckOutTime());
        return responseDTO;
    }
}
