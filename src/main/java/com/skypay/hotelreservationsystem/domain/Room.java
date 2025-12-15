package com.skypay.hotelreservationsystem.domain;

import com.skypay.hotelreservationsystem.domain.enums.RoomType;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Data
public class Room {
    @Setter(lombok.AccessLevel.NONE)
    private final int roomNumber;
    private RoomType roomType;
    private int pricePerNight;
    @Setter(lombok.AccessLevel.NONE)
    private final Date createdAt = new Date();
}
