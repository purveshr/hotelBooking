package com.oyo.hotelBooking.services;

import com.oyo.hotelBooking.dtos.PaymentRequestDTO;
import com.oyo.hotelBooking.dtos.PaymentResponseDTO;
import com.oyo.hotelBooking.entity.Booking;
import com.oyo.hotelBooking.entity.Payment;
import com.oyo.hotelBooking.enums.PaymentStatus;
import com.oyo.hotelBooking.repository.BookingRepository;
import com.oyo.hotelBooking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private com.oyo.hotelBooking.security.CurrentUserService currentUserService;

    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Booking with id " + dto.getBookingId() + " not found"));

        // Authorization: only booking customer or admin can create payment
        com.oyo.hotelBooking.entity.User current = currentUserService.getCurrentUser();
        boolean isCustomer = booking.getCustomer() != null && booking.getCustomer().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == com.oyo.hotelBooking.enums.Roles.ADMIN;
        if (!(isCustomer || isAdmin)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to create payment for this booking");
        }

        // Basic validation: amount should be >= 0 and ideally equal booking amount
        if (dto.getAmount().compareTo(booking.getTotalAmount()) != 0) {
            // Allow payments that match booking amount; reject otherwise
            throw new IllegalArgumentException("Payment amount must equal booking total amount: " + booking.getTotalAmount());
        }

        Payment payment = Payment.builder()
                .amount(dto.getAmount())
                .paymentStatus(dto.getPaymentStatus())
                .transactionId(dto.getTransactionId() != null ? dto.getTransactionId() : "TXN-" + System.currentTimeMillis())
                .paymentDate(LocalDateTime.now())
                .booking(booking)
                .build();

        Payment saved = paymentRepository.save(payment);

        // update booking payment link and status
        booking.setPayment(saved);
        booking.setPaymentStatus(dto.getPaymentStatus());
        if (dto.getPaymentStatus() == PaymentStatus.PAID) {
            booking.setBookingStatus(com.oyo.hotelBooking.enums.BookingStatus.CONFIRMED);
        }
        bookingRepository.save(booking);

        return convertToResponseDTO(saved);
    }

    public PaymentResponseDTO getPaymentById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new com.oyo.hotelBooking.exceptionHandler.ResourceNotFoundException("Payment with id " + id + " not found"));

        // Authorization: allow payment owner (booking customer), hotel owner, or ADMIN
        com.oyo.hotelBooking.entity.User current = currentUserService.getCurrentUser();
        boolean isCustomer = payment.getBooking() != null && payment.getBooking().getCustomer() != null && payment.getBooking().getCustomer().getId().equals(current.getId());
        boolean isHotelOwner = payment.getBooking() != null && payment.getBooking().getRoom() != null && payment.getBooking().getRoom().getHotel() != null && payment.getBooking().getRoom().getHotel().getOwner().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == com.oyo.hotelBooking.enums.Roles.ADMIN;
        if (!(isCustomer || isHotelOwner || isAdmin)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not allowed to view this payment");
        }

        return convertToResponseDTO(payment);
    }

    private PaymentResponseDTO convertToResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentDate(payment.getPaymentDate());

        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
            dto.setBookingCode(payment.getBooking().getBookingCode());
        }

        return dto;
    }
}

