
package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.BookingRequestDTO;
import com.oyo.hotelBooking.dtos.BookingResponseDTO;
import com.oyo.hotelBooking.dtos.BookingStatusUpdateDTO;
import com.oyo.hotelBooking.entity.Booking;
import com.oyo.hotelBooking.entity.Room;
import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.enums.BookingStatus;
import com.oyo.hotelBooking.enums.Roles;
import com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException;
import com.oyo.hotelBooking.repository.BookingRepository;
import com.oyo.hotelBooking.repository.RoomRepository;
import com.oyo.hotelBooking.repository.UserRepository;
import com.oyo.hotelBooking.security.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final CurrentUserService currentUserService;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          RoomRepository roomRepository,
                          CurrentUserService currentUserService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.currentUserService = currentUserService;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        User current = currentUserService.getCurrentUser();

        if (current.getRole() != Roles.CUSTOMER) {
            throw new AccessDeniedException("Only CUSTOMER can create bookings");
        }

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room with id " + dto.getRoomId() + " not found"
                ));

        // Keep this only as manual inventory availability check
        if (!Boolean.TRUE.equals(room.getAvailable())) {
            throw new IllegalStateException("Room with id " + dto.getRoomId() + " is currently not open for booking");
        }

        if (dto.getTotalGuests() > room.getCapacity()) {
            throw new IllegalArgumentException("Total guests exceed room capacity");
        }

        long nights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        LocalTime hotelCheckIn = LocalTime.of(15, 0);
        LocalTime hotelCheckOut = LocalTime.of(11, 0);

        if (room.getHotel() != null) {
            if (room.getHotel().getCheckInTime() != null) {
                hotelCheckIn = room.getHotel().getCheckInTime();
            }
            if (room.getHotel().getCheckOutTime() != null) {
                hotelCheckOut = room.getHotel().getCheckOutTime();
            }
        }

        LocalDateTime checkInDateTime = dto.getCheckInDate().atTime(hotelCheckIn);
        LocalDateTime checkOutDateTime = dto.getCheckOutDate().atTime(hotelCheckOut);

        boolean hasOverlap = bookingRepository
                .existsByRoomIdAndBookingStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        room.getId(),
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                        checkOutDateTime,
                        checkInDateTime
                );

        if (hasOverlap) {
            throw new IllegalStateException("Room is already booked for the selected dates");
        }

        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .bookingCode("BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .checkInDate(checkInDateTime)
                .checkOutDate(checkOutDateTime)
                .totalGuests(dto.getTotalGuests())
                .totalAmount(totalAmount)
                .customer(current)
                .room(room)
                .build();

        Booking saved = bookingRepository.save(booking);
        return convertToResponseDTO(saved);
    }



    public BookingResponseDTO getBookingById(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));

        User current = currentUserService.getCurrentUser();

        boolean isCustomer = booking.getCustomer() != null
                && booking.getCustomer().getId().equals(current.getId());

        boolean isHotelOwner = booking.getRoom() != null
                && booking.getRoom().getHotel() != null
                && booking.getRoom().getHotel().getOwner().getId().equals(current.getId());

        boolean isAdmin = current.getRole() == Roles.ADMIN;

        if (!(isCustomer || isHotelOwner || isAdmin)) {
            throw new AccessDeniedException("You are not allowed to view this booking");
        }

        return convertToResponseDTO(booking);
    }

    public List<BookingResponseDTO> listBookingsByUser(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        User current = currentUserService.getCurrentUser();
        boolean isSameUser = current.getId().equals(userId);
        boolean isAdmin = current.getRole() == Roles.ADMIN;

        if (!(isSameUser || isAdmin)) {
            throw new AccessDeniedException("You are not allowed to view these bookings");
        }

        return bookingRepository.findByCustomerId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public BookingResponseDTO updateBookingStatus(Integer id, BookingStatusUpdateDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));

        User current = currentUserService.getCurrentUser();

        boolean isCustomer = booking.getCustomer() != null
                && booking.getCustomer().getId().equals(current.getId());

        boolean isHotelOwner = booking.getRoom() != null
                && booking.getRoom().getHotel() != null
                && booking.getRoom().getHotel().getOwner() != null
                && booking.getRoom().getHotel().getOwner().getId().equals(current.getId());

        boolean isAdmin = current.getRole() == Roles.ADMIN;

        if (!(isCustomer || isHotelOwner || isAdmin)) {
            throw new AccessDeniedException("You are not allowed to update this booking");
        }

        BookingStatus newStatus = dto.getBookingStatus();
        BookingStatus currentStatus = booking.getBookingStatus();

        validateStatusTransition(current, isCustomer, isHotelOwner, isAdmin, currentStatus, newStatus);

        booking.setBookingStatus(newStatus);

        booking.setBookingStatus(newStatus);

        Booking updated = bookingRepository.save(booking);
        return convertToResponseDTO(updated);
    }

    private void validateStatusTransition(User current,
                                          boolean isCustomer,
                                          boolean isHotelOwner,
                                          boolean isAdmin,
                                          BookingStatus currentStatus,
                                          BookingStatus newStatus) {

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException("Booking already has this status");
        }

        if (isAdmin) {
            return;
        }

        if (isCustomer) {
            if (newStatus != BookingStatus.CANCELLED) {
                throw new AccessDeniedException("Customer can only cancel their own booking");
            }

            if (currentStatus == BookingStatus.COMPLETED) {
                throw new IllegalArgumentException("Completed booking cannot be cancelled");
            }

            return;
        }

        if (isHotelOwner) {
            if (newStatus != BookingStatus.CONFIRMED
                    && newStatus != BookingStatus.CANCELLED
                    && newStatus != BookingStatus.COMPLETED) {
                throw new AccessDeniedException("Hotel owner cannot set this booking status");
            }

            return;
        }

        throw new AccessDeniedException("Invalid booking status update");
    }

    private BookingResponseDTO convertToResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setBookingCode(booking.getBookingCode());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setTotalGuests(booking.getTotalGuests());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());

        if (booking.getCustomer() != null) {
            dto.setCustomerId(booking.getCustomer().getId());
            dto.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
        }

        if (booking.getRoom() != null) {
            dto.setRoomId(booking.getRoom().getId());
            dto.setRoomNumber(booking.getRoom().getRoomNumber());

            if (booking.getRoom().getHotel() != null) {
                dto.setHotelId(booking.getRoom().getHotel().getId());
                dto.setHotelName(booking.getRoom().getHotel().getName());
            }
        }

        return dto;
    }
}
