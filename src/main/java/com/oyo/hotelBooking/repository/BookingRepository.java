package com.oyo.hotelBooking.repository;

import com.oyo.hotelBooking.entity.Booking;
import com.oyo.hotelBooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCustomerId(Integer customerId);

    Booking findByBookingCode(String bookingCode);

    boolean existsByRoomIdAndBookingStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Integer roomId,
            List<BookingStatus> statuses,
            LocalDateTime requestedCheckOut,
            LocalDateTime requestedCheckIn
    );
}
