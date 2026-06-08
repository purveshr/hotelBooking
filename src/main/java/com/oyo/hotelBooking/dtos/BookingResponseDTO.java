package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.BookingStatus;
import com.oyo.hotelBooking.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingResponseDTO {

    private Integer id;
    private String bookingCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalGuests;
    private BigDecimal totalAmount;

    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;

    private Integer customerId;
    private String customerName;

    private Integer roomId;
    private String roomNumber;

    private Integer hotelId;
    private String hotelName;
}