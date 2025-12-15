package com.skypay.hotelreservationsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Data
public class User {
    @Setter(lombok.AccessLevel.NONE)
    private final int userId;
    private int balance;
    @Setter(lombok.AccessLevel.NONE)
    private final Date createdAt = new Date();
}
