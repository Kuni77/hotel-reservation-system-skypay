package com.skypay.hotelreservationsystem.service;

import com.skypay.hotelreservationsystem.domain.Booking;
import com.skypay.hotelreservationsystem.domain.Room;
import com.skypay.hotelreservationsystem.domain.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // ========== ROOM TESTS ==========

    @Test
    @DisplayName("Should create a new room successfully")
    void testCreateRoom() {
        service.setRoom(1, RoomType.STANDARD, 1000);

        assertEquals(1, service.rooms.size());
        assertEquals(1, service.rooms.get(0).getRoomNumber());
        assertEquals(RoomType.STANDARD, service.rooms.get(0).getRoomType());
        assertEquals(1000, service.rooms.get(0).getPricePerNight());
    }

    @Test
    @DisplayName("Should update existing room without creating duplicate")
    void testUpdateRoom() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(1, RoomType.SUITE, 2000);

        assertEquals(1, service.rooms.size());
        assertEquals(RoomType.SUITE, service.rooms.get(0).getRoomType());
        assertEquals(2000, service.rooms.get(0).getPricePerNight());
    }

    @Test
    @DisplayName("Should throw exception for invalid room number")
    void testInvalidRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(-1, RoomType.STANDARD, 1000);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(0, RoomType.STANDARD, 1000);
        });
    }

    @Test
    @DisplayName("Should throw exception for negative price")
    void testNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.setRoom(1, RoomType.STANDARD, -100);
        });
    }

    // ========== USER TESTS ==========

    @Test
    @DisplayName("Should create a new user successfully")
    void testCreateUser() {
        service.setUser(1, 5000);

        assertEquals(1, service.users.size());
        assertEquals(1, service.users.get(0).getUserId());
        assertEquals(5000, service.users.get(0).getBalance());
    }

    @Test
    @DisplayName("Should update existing user balance")
    void testUpdateUserBalance() {
        service.setUser(1, 5000);
        service.setUser(1, 10000);

        assertEquals(1, service.users.size());
        assertEquals(10000, service.users.get(0).getBalance());
    }

    @Test
    @DisplayName("Should throw exception for invalid user ID")
    void testInvalidUserId() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.setUser(-1, 5000);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            service.setUser(0, 5000);
        });
    }

    @Test
    @DisplayName("Should throw exception for negative balance")
    void testNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.setUser(1, -100);
        });
    }

    // ========== BOOKING TESTS ==========

    @Test
    @DisplayName("Should book room successfully with sufficient balance")
    void testSuccessfulBooking() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 8);

        assertDoesNotThrow(() -> {
            service.bookRoom(1, 1, checkIn, checkOut);
        });

        assertEquals(1, service.bookings.size());
        assertEquals(4000, service.users.get(0).getBalance()); // 5000 - 1000
    }

    @Test
    @DisplayName("Should throw exception for insufficient balance")
    void testInsufficientBalance() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 500); // Not enough for 1 night

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 8);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 1, checkIn, checkOut);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    @DisplayName("Should throw exception for invalid dates")
    void testInvalidDates() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 6); // Before check-in

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 1, checkIn, checkOut);
        });

        assertTrue(exception.getMessage().contains("after check-in"));
    }

    @Test
    @DisplayName("Should throw exception for same check-in and check-out dates")
    void testSameDates() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 7);

        assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 1, checkIn, checkOut);
        });
    }

    @Test
    @DisplayName("Should throw exception for non-existent user")
    void testNonExistentUser() {
        service.setRoom(1, RoomType.STANDARD, 1000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 8);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(999, 1, checkIn, checkOut);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should throw exception for non-existent room")
    void testNonExistentRoom() {
        service.setUser(1, 5000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 8);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(1, 999, checkIn, checkOut);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should detect overlapping bookings")
    void testOverlappingBookings() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        service.setUser(2, 5000);

        // First booking
        Date checkIn1 = createDate(2026, 7, 7);
        Date checkOut1 = createDate(2026, 7, 10);
        service.bookRoom(1, 1, checkIn1, checkOut1);

        // Overlapping booking
        Date checkIn2 = createDate(2026, 7, 8);
        Date checkOut2 = createDate(2026, 7, 11);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.bookRoom(2, 1, checkIn2, checkOut2);
        });

        assertTrue(exception.getMessage().contains("not available"));
    }

    @Test
    @DisplayName("Should allow consecutive bookings")
    void testConsecutiveBookings() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        service.setUser(2, 5000);

        // First booking
        Date checkIn1 = createDate(2026, 7, 7);
        Date checkOut1 = createDate(2026, 7, 10);
        service.bookRoom(1, 1, checkIn1, checkOut1);

        // Consecutive booking (starts when first ends)
        Date checkIn2 = createDate(2026, 7, 10);
        Date checkOut2 = createDate(2026, 7, 13);

        assertDoesNotThrow(() -> {
            service.bookRoom(2, 1, checkIn2, checkOut2);
        });

        assertEquals(2, service.bookings.size());
    }

    @Test
    @DisplayName("Should calculate correct price for multiple nights")
    void testMultipleNightsPricing() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 10000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 12); // 5 nights

        service.bookRoom(1, 1, checkIn, checkOut);

        assertEquals(5000, service.users.get(0).getBalance()); // 10000 - 5000
        assertEquals(5000, service.bookings.get(0).getTotalPrice());
    }

    // ========== SNAPSHOT TESTS ==========

    @Test
    @DisplayName("Should preserve booking data when room is updated")
    void testBookingSnapshotIntegrity() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 8);

        service.bookRoom(1, 1, checkIn, checkOut);

        // Update room
        service.setRoom(1, RoomType.SUITE, 5000);

        // Booking should still have old data
        Booking booking = service.bookings.get(0);
        assertEquals(RoomType.STANDARD, booking.getRoomTypeSnapshot());
        assertEquals(1000, booking.getRoomPriceSnapshot());
        assertEquals(5000, booking.getUserBalanceSnapshot());

        // Room should have new data
        Room room = service.rooms.get(0);
        assertEquals(RoomType.SUITE, room.getRoomType());
        assertEquals(5000, room.getPricePerNight());
    }

    @Test
    @DisplayName("Should store correct user balance at booking time")
    void testUserBalanceSnapshot() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);

        Date checkIn = createDate(2026, 7, 7);
        Date checkOut = createDate(2026, 7, 8);

        service.bookRoom(1, 1, checkIn, checkOut);

        Booking booking = service.bookings.get(0);
        assertEquals(5000, booking.getUserBalanceSnapshot()); // Balance before booking
        assertEquals(4000, service.users.get(0).getBalance()); // Current balance
    }

    // ========== SORTING TESTS ==========

    @Test
    @DisplayName("Should display rooms from latest to oldest")
    void testRoomsSortedByCreationDate() throws InterruptedException {
        service.setRoom(1, RoomType.STANDARD, 1000);
        Thread.sleep(10); // Small delay to ensure different timestamps
        service.setRoom(2, RoomType.JUNIOR, 2000);
        Thread.sleep(10);
        service.setRoom(3, RoomType.SUITE, 3000);

        // Latest should be room 3
        assertTrue(service.rooms.get(2).getCreatedAt()
                .after(service.rooms.get(0).getCreatedAt()));
    }

    @Test
    @DisplayName("Should display bookings from latest to oldest")
    void testBookingsSortedByCreationDate() throws InterruptedException {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 10000);

        Date checkIn1 = createDate(2026, 7, 7);
        Date checkOut1 = createDate(2026, 7, 8);
        service.bookRoom(1, 1, checkIn1, checkOut1);

        Thread.sleep(10);

        Date checkIn2 = createDate(2026, 7, 10);
        Date checkOut2 = createDate(2026, 7, 11);
        service.bookRoom(1, 1, checkIn2, checkOut2);

        assertTrue(service.bookings.get(1).getCreatedAt()
                .after(service.bookings.get(0).getCreatedAt()));
    }
}
