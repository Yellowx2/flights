package com.ryanair.flights.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Leg {

    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
}
