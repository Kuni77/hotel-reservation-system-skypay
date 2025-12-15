package com.skypay.hotelreservationsystem;

import com.skypay.hotelreservationsystem.domain.enums.RoomType;
import com.skypay.hotelreservationsystem.service.Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class HotelReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelReservationSystemApplication.class, args);

        Service service = new Service();

        System.out.println("=== SKYPAY HOTEL RESERVATION SYSTEM TEST ===\n");

        try {
            // Create 3 rooms
            System.out.println("--- Creating Rooms ---");
            service.setRoom(1, RoomType.STANDARD, 1000);
            System.out.println(" Room 1 created (STANDARD, 1000/night)");

            service.setRoom(2, RoomType.JUNIOR, 2000);
            System.out.println(" Room 2 created (JUNIOR, 2000/night)");

            service.setRoom(3, RoomType.SUITE, 3000);
            System.out.println(" Room 3 created (SUITE, 3000/night)");
            System.out.println();

            // Create 2 users
            System.out.println("--- Creating Users ---");
            service.setUser(1, 5000);
            System.out.println(" User 1 created (Balance: 5000)");

            service.setUser(2, 10000);
            System.out.println(" User 2 created (Balance: 10000)");
            System.out.println();

            // Booking attempts
            System.out.println("--- Booking Attempts ---");

            // User 1 books Room 2 from 30/06/2026 to 07/07/2026 (7 nights)
            try {
                service.bookRoom(1, 2, createDate(2026, 6, 30), createDate(2026, 7, 7));
                System.out.println("x User 1 booking Room 2 (30/06-07/07): FAILED - Insufficient balance");
            } catch (Exception e) {
                System.out.println("x User 1 booking Room 2 (30/06-07/07): " + e.getMessage());
            }

            // User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026 (invalid dates)
            try {
                service.bookRoom(1, 2, createDate(2026, 7, 7), createDate(2026, 6, 30));
                System.out.println(" User 1 booking Room 2 (07/07-30/06): SUCCESS");
            } catch (Exception e) {
                System.out.println("x User 1 booking Room 2 (07/07-30/06): " + e.getMessage());
            }

            // User 1 books Room 1 from 07/07/2026 to 08/07/2026 (1 night)
            try {
                service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
                System.out.println(" User 1 booking Room 1 (07/07-08/07): SUCCESS");
            } catch (Exception e) {
                System.out.println("x User 1 booking Room 1 (07/07-08/07): " + e.getMessage());
            }

            // User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights)
            try {
                service.bookRoom(2, 1, createDate(2026, 7, 7), createDate(2026, 7, 9));
                System.out.println(" User 2 booking Room 1 (07/07-09/07): SUCCESS");
            } catch (Exception e) {
                System.out.println("x User 2 booking Room 1 (07/07-09/07): " + e.getMessage());
            }

            // User 2 books Room 3 from 07/07/2026 to 08/07/2026 (1 night)
            try {
                service.bookRoom(2, 3, createDate(2026, 7, 7), createDate(2026, 7, 8));
                System.out.println(" User 2 booking Room 3 (07/07-08/07): SUCCESS");
            } catch (Exception e) {
                System.out.println("x User 2 booking Room 3 (07/07-08/07): " + e.getMessage());
            }

            System.out.println();

            // Update Room 1
            System.out.println("--- Updating Room 1 ---");
            service.setRoom(1, RoomType.SUITE, 10000);
            System.out.println(" Room 1 updated to SUITE with price 10000/night");
            System.out.println();

            // Print all data
            System.out.println("--- Final Results ---\n");
            service.printAll();
            System.out.println();
            service.printAllUsers();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
