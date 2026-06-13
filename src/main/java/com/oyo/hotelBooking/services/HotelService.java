
package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.HotelRequestDTO;
import com.oyo.hotelBooking.dtos.HotelResponseDTO;
import com.oyo.hotelBooking.entity.Hotel;
import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.enums.Roles;
import com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException;
import com.oyo.hotelBooking.repository.HotelRepository;
import com.oyo.hotelBooking.repository.UserRepository;
import com.oyo.hotelBooking.security.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public HotelService(HotelRepository hotelRepository,
                        UserRepository userRepository,
                        CurrentUserService currentUserService) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    public HotelResponseDTO createHotel(HotelRequestDTO hotelRequestDTO) {
        User current = currentUserService.getCurrentUser();

        User owner = userRepository.findById(hotelRequestDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Owner with id " + hotelRequestDTO.getOwnerId() + " not found"
                ));

        validateCreateOrOwnerAssignment(current, owner);

        Hotel hotel = Hotel.builder()
                .name(hotelRequestDTO.getName())
                .address(hotelRequestDTO.getAddress())
                .city(hotelRequestDTO.getCity())
                .state(hotelRequestDTO.getState())
                .country(hotelRequestDTO.getCountry())
                .description(hotelRequestDTO.getDescription())
                .checkInTime(hotelRequestDTO.getCheckInTime() != null ? hotelRequestDTO.getCheckInTime() : LocalTime.of(15, 0))
                .checkOutTime(hotelRequestDTO.getCheckOutTime() != null ? hotelRequestDTO.getCheckOutTime() : LocalTime.of(11, 0))
                .owner(owner)
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToResponseDTO(savedHotel);
    }

    public List<HotelResponseDTO> createMultipleHotels(List<HotelRequestDTO> hotelRequestDTOs) {
        User current = currentUserService.getCurrentUser();

        List<Hotel> hotels = hotelRequestDTOs.stream().map(hotelRequestDTO -> {
            User owner = userRepository.findById(hotelRequestDTO.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Owner with id " + hotelRequestDTO.getOwnerId() + " not found"
                    ));

            validateCreateOrOwnerAssignment(current, owner);

            return Hotel.builder()
                    .name(hotelRequestDTO.getName())
                    .address(hotelRequestDTO.getAddress())
                    .city(hotelRequestDTO.getCity())
                    .state(hotelRequestDTO.getState())
                    .country(hotelRequestDTO.getCountry())
                    .description(hotelRequestDTO.getDescription())
                    .checkInTime(hotelRequestDTO.getCheckInTime() != null ? hotelRequestDTO.getCheckInTime() : LocalTime.of(15, 0))
                    .checkOutTime(hotelRequestDTO.getCheckOutTime() != null ? hotelRequestDTO.getCheckOutTime() : LocalTime.of(11, 0))
                    .owner(owner)
                    .build();
        }).collect(Collectors.toList());

        return hotelRepository.saveAll(hotels)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<HotelResponseDTO> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public HotelResponseDTO getHotelById(Integer id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + id + " not found"));
        return convertToResponseDTO(hotel);
    }

    public HotelResponseDTO updateHotel(Integer id, HotelRequestDTO hotelRequestDTO) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + id + " not found"));

        User current = currentUserService.getCurrentUser();

        boolean isAdmin = current.getRole() == Roles.ADMIN;
        boolean isOwner = hotel.getOwner() != null && hotel.getOwner().getId().equals(current.getId());

        if (!(isAdmin || isOwner)) {
            throw new AccessDeniedException("You are not allowed to update this hotel");
        }

        User requestedOwner = userRepository.findById(hotelRequestDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Owner with id " + hotelRequestDTO.getOwnerId() + " not found"
                ));

        if (!isAdmin && !requestedOwner.getId().equals(current.getId())) {
            throw new AccessDeniedException("Hotel owner can only keep ownership assigned to themselves");
        }

        if (requestedOwner.getRole() != Roles.HOTEL_OWNER) {
            throw new IllegalArgumentException("Assigned owner must have HOTEL_OWNER role");
        }

        hotel.setName(hotelRequestDTO.getName());
        hotel.setAddress(hotelRequestDTO.getAddress());
        hotel.setCity(hotelRequestDTO.getCity());
        hotel.setState(hotelRequestDTO.getState());
        hotel.setCountry(hotelRequestDTO.getCountry());
        hotel.setDescription(hotelRequestDTO.getDescription());
        hotel.setCheckInTime(hotelRequestDTO.getCheckInTime() != null ? hotelRequestDTO.getCheckInTime() : LocalTime.of(15, 0));
        hotel.setCheckOutTime(hotelRequestDTO.getCheckOutTime() != null ? hotelRequestDTO.getCheckOutTime() : LocalTime.of(11, 0));
        hotel.setOwner(requestedOwner);

        return convertToResponseDTO(hotelRepository.save(hotel));
    }

    public String deleteHotel(Integer id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + id + " not found"));

        User current = currentUserService.getCurrentUser();

        boolean isAdmin = current.getRole() == Roles.ADMIN;
        boolean isOwner = hotel.getOwner() != null && hotel.getOwner().getId().equals(current.getId());

        if (!(isAdmin || isOwner)) {
            throw new AccessDeniedException("You are not allowed to delete this hotel");
        }

        hotelRepository.delete(hotel);
        return "Hotel with id " + id + " deleted successfully";
    }

    private void validateCreateOrOwnerAssignment(User current, User owner) {
        if (owner.getRole() != Roles.HOTEL_OWNER) {
            throw new IllegalArgumentException("Assigned owner must have HOTEL_OWNER role");
        }

        if (current.getRole() == Roles.HOTEL_OWNER && !current.getId().equals(owner.getId())) {
            throw new AccessDeniedException("HOTEL_OWNER can only create hotels for themselves");
        }

        if (current.getRole() == Roles.CUSTOMER) {
            throw new AccessDeniedException("CUSTOMER is not allowed to create hotels");
        }
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
