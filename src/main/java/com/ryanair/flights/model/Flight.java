package com.ryanair.flights.model;

import lombok.Data;

/**
 * Model for schedule API with flight's data
 */
@Data
public class Flight {
    private String carrierCode;
    private String number;
    private String departureTime;
    private String arrivalTime;
}
