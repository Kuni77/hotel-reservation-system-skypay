package com.skypay.hotelreservationsystem.service;

import com.skypay.hotelreservationsystem.domain.Booking;
import com.skypay.hotelreservationsystem.domain.Room;
import com.skypay.hotelreservationsystem.domain.User;
import com.skypay.hotelreservationsystem.domain.enums.RoomType;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.skypay.hotelreservationsystem.util.Utils.normalizeDate;

@NoArgsConstructor
public class Service {
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    private int bookingIdCounter = 1;

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (roomPricePerNight < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        Room existingRoom = findRoom(roomNumber);

        if (existingRoom != null) {
            // Update existing room - this does NOT affect previous bookings
            existingRoom.setRoomType(roomType);
            existingRoom.setPricePerNight(roomPricePerNight);
        } else {
            // Create new room
            rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
        }
    }

    public void setUser(int userId, int balance) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        User existingUser = findUser(userId);

        if (existingUser != null) {
            existingUser.setBalance(balance);
        } else {
            users.add(new User(userId, balance));
        }
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        // Find user and room
        User user = findUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room with number " + roomNumber + " not found");
        }

        // Normalize dates to only consider year, month, day
        Date normalizedCheckIn = normalizeDate(checkIn);
        Date normalizedCheckOut = normalizeDate(checkOut);

        // Validate dates
        if (normalizedCheckOut.compareTo(normalizedCheckIn) <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Calculate number of nights and total price
        long diffInMillis = normalizedCheckOut.getTime() - normalizedCheckIn.getTime();
        int nights = (int) (diffInMillis / (1000 * 60 * 60 * 24));
        int totalPrice = nights * room.getPricePerNight();

        // Check if user has sufficient balance
        if (user.getBalance() < totalPrice) {
            throw new IllegalArgumentException(
                    "Insufficient balance. Required: " + totalPrice + ", Available: " + user.getBalance()
            );
        }

        // Check room availability
        if (!isRoomAvailable(roomNumber, normalizedCheckIn, normalizedCheckOut)) {
            throw new IllegalArgumentException(
                    "Room " + roomNumber + " is not available for the selected period"
            );
        }

        // Create booking with snapshot of current data
        Booking booking = new Booking(
                bookingIdCounter++,
                userId,
                roomNumber,
                normalizedCheckIn,
                normalizedCheckOut,
                totalPrice,
                room,
                user
        );

        bookings.add(booking);

        // Update user balance
        user.setBalance(user.getBalance() - totalPrice);
    }

    public void printAll() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("========== ALL ROOMS (Latest to Oldest) ==========");

        // Sort rooms by creation date (latest first)
        rooms.stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .forEach(room -> {
                    System.out.printf("Room %d | Type: %s | Price/Night: %d%n",
                            room.getRoomNumber(),
                            room.getRoomType(),
                            room.getPricePerNight()
                    );
                });

        System.out.println("\n========== ALL BOOKINGS (Latest to Oldest) ==========");

        // Sort bookings by creation date (latest first)
        bookings.stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .forEach(booking -> {
                    System.out.printf("%nBooking ID: %d%n", booking.getBookingId());
                    System.out.printf("  User ID: %d (Balance at booking: %d)%n",
                            booking.getUserId(),
                            booking.getUserBalanceSnapshot()
                    );
                    System.out.printf("  Room: %d | Type: %s | Price/Night: %d%n",
                            booking.getRoomNumber(),
                            booking.getRoomTypeSnapshot(),
                            booking.getRoomPriceSnapshot()
                    );
                    System.out.printf("  Check-in: %s%n", dateFormat.format(booking.getCheckIn()));
                    System.out.printf("  Check-out: %s%n", dateFormat.format(booking.getCheckOut()));
                    System.out.printf("  Total Price: %d%n", booking.getTotalPrice());
                });
    }

    public void printAllUsers() {
        System.out.println("========== ALL USERS (Latest to Oldest) ==========");

        // Sort users by creation date (latest first)
        users.stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .forEach(user -> {
                    System.out.printf("User ID: %d | Balance: %d%n",
                            user.getUserId(),
                            user.getBalance()
                    );
                });
    }

    // Helper methods
    private Room findRoom(int roomNumber) {
        return rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .orElse(null);
    }

    private User findUser(int userId) {
        return users.stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    private boolean isRoomAvailable(int roomNumber, Date checkIn, Date checkOut) {
        return bookings.stream()
                .filter(b -> b.getRoomNumber() == roomNumber)
                .allMatch(booking -> {
                    // Room is available if:
                    // - New booking ends before existing booking starts, OR
                    // - New booking starts after existing booking ends
                    return checkOut.compareTo(booking.getCheckIn()) <= 0 ||
                            checkIn.compareTo(booking.getCheckOut()) >= 0;
                });
    }
}
