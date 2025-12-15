package com.skypay.hotelreservationsystem.domain;

import com.skypay.hotelreservationsystem.domain.enums.RoomType;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;

import static com.skypay.hotelreservationsystem.util.Utils.normalizeDate;

@Getter
public class Booking {
    private final int bookingId;
    private final int userId;
    private final int roomNumber;
    private final Date checkIn;
    private final Date checkOut;
    private final int totalPrice;
    private final Date createdAt;

    // Snapshot of room data at booking time
    private final RoomType roomTypeSnapshot;
    private final int roomPriceSnapshot;

    // Snapshot of user balance at booking time
    private final int userBalanceSnapshot;

    public Booking(int bookingId, int userId, int roomNumber, Date checkIn, Date checkOut,
                   int totalPrice, Room room, User user) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkIn = normalizeDate(checkIn);
        this.checkOut = normalizeDate(checkOut);
        this.totalPrice = totalPrice;
        this.createdAt = new Date();

        // Store snapshot of room data
        this.roomTypeSnapshot = room.getRoomType();
        this.roomPriceSnapshot = room.getPricePerNight();

        // Store snapshot of user balance
        this.userBalanceSnapshot = user.getBalance();
    }
}
