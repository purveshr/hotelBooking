package com.oyo.hotelBooking.dtos;

import com.oyo.hotelBooking.enums.HotelStatus;
import lombok.Data;

@Data
public class HotelResponseDTO {

    private Integer id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String description;
    private HotelStatus status;

    private Integer ownerId;
    private String ownerName;
}