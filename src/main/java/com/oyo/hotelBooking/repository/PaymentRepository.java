package com.oyo.hotelBooking.repository;

import com.oyo.hotelBooking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByBookingId(Integer bookingId);
}

