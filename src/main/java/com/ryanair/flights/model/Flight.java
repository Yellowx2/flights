package com.ryanair.flights.model;

import java.time.LocalTime;

import lombok.Data;

/**
 * Model for schedule API with flight's data
 */
@Data
public class Flight {
    private String carrierCode;
    private String number;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public void setDepartureTime(String departureTime) {
        this.departureTime = LocalTime.parse(departureTime);
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = LocalTime.parse(arrivalTime);
    }
}
