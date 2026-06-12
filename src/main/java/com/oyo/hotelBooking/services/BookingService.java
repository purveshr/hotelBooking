package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.BookingRequestDTO;
import com.oyo.hotelBooking.dtos.BookingResponseDTO;
import com.oyo.hotelBooking.dtos.BookingStatusUpdateDTO;
import com.oyo.hotelBooking.entity.Booking;
import com.oyo.hotelBooking.entity.Room;
import com.oyo.hotelBooking.entity.User;
import com.oyo.hotelBooking.enums.BookingStatus;
import com.oyo.hotelBooking.repository.BookingRepository;
import com.oyo.hotelBooking.repository.RoomRepository;
import com.oyo.hotelBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private com.oyo.hotelBooking.security.CurrentUserService currentUserService;

    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Customer with id " + dto.getCustomerId() + " not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Room with id " + dto.getRoomId() + " not found"));

        if (!room.getAvailable()) {
            throw new RuntimeException("Room with id " + dto.getRoomId() + " is not available");
        }

        long nights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Use hotel's configured check-in/check-out times if available, else fall back to defaults
        LocalTime hotelCheckIn = LocalTime.of(15, 0);
        LocalTime hotelCheckOut = LocalTime.of(11, 0);
        if (room.getHotel() != null) {
            if (room.getHotel().getCheckInTime() != null) hotelCheckIn = room.getHotel().getCheckInTime();
            if (room.getHotel().getCheckOutTime() != null) hotelCheckOut = room.getHotel().getCheckOutTime();
        }

        LocalDateTime checkInDateTime = dto.getCheckInDate().atTime(hotelCheckIn);
        LocalDateTime checkOutDateTime = dto.getCheckOutDate().atTime(hotelCheckOut);

        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .bookingCode("BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .checkInDate(checkInDateTime)
                .checkOutDate(checkOutDateTime)
                .totalGuests(dto.getTotalGuests())
                .totalAmount(totalAmount)
                .customer(customer)
                .room(room)
                .build();

        // mark room unavailable
        room.setAvailable(false);

        Booking saved = bookingRepository.save(booking);
        // room saved via cascading? ensure room persisted explicitly
        roomRepository.save(room);

        return convertToResponseDTO(saved);
    }

    public BookingResponseDTO getBookingById(Integer id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Booking with id " + id + " not found"));
        // Authorization: allow booking owner (customer), hotel owner, or ADMIN
        com.oyo.hotelBooking.entity.User current = currentUserService.getCurrentUser();
        boolean isCustomer = booking.getCustomer() != null && booking.getCustomer().getId().equals(current.getId());
        boolean isHotelOwner = booking.getRoom() != null && booking.getRoom().getHotel() != null && booking.getRoom().getHotel().getOwner().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == com.oyo.hotelBooking.enums.Roles.ADMIN;
        if (!(isCustomer || isHotelOwner || isAdmin)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to view this booking");
        }

        return convertToResponseDTO(booking);
    }

    public List<BookingResponseDTO> listBookingsByUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("User with id " + userId + " not found"));

        com.oyo.hotelBooking.entity.User current = currentUserService.getCurrentUser();
        boolean isSameUser = current.getId().equals(userId);
        boolean isAdmin = current.getRole() == com.oyo.hotelBooking.enums.Roles.ADMIN;
        if (!(isSameUser || isAdmin)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to view these bookings");
        }

        List<Booking> bookings = bookingRepository.findByCustomerId(userId);
        return bookings.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public BookingResponseDTO updateBookingStatus(Integer id, BookingStatusUpdateDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Booking with id " + id + " not found"));

        BookingStatus newStatus = dto.getBookingStatus();
        booking.setBookingStatus(newStatus);

        // If cancelled, free up the room
        if (newStatus == BookingStatus.CANCELLED) {
            Room room = booking.getRoom();
            if (room != null) {
                room.setAvailable(true);
                roomRepository.save(room);
            }
        }

        Booking updated = bookingRepository.save(booking);
        return convertToResponseDTO(updated);
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

