package com.ryanair.flights.service;

import java.time.LocalDateTime;
import java.util.Set;

import com.ryanair.flights.model.Interconnection;

public interface IFlight {

    Set<Interconnection> getScheduledFlights(String arrival, String departure, LocalDateTime arrivalDateTime,
            LocalDateTime departureDateTime);
}
