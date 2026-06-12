package com.oyo.hotelBooking.entity;

import com.oyo.hotelBooking.enums.HotelStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;
    private String state;
    private String country;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HotelStatus status = HotelStatus.ACTIVE;

    @Builder.Default
    @Column(name = "check_in_time", nullable = false)
    private LocalTime checkInTime = LocalTime.of(15, 0);

    @Builder.Default
    @Column(name = "check_out_time", nullable = false)
    private LocalTime checkOutTime = LocalTime.of(11, 0);

}

