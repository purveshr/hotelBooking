package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.RoomType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomResponseDTO {

    private Integer id;
    private String roomNumber;
    private RoomType roomType;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private Boolean available;

    private Integer hotelId;
    private String hotelName;
}