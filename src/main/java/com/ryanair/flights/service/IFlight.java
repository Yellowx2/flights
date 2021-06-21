package com.ryanair.flights.service;

import java.time.LocalDateTime;

import com.ryanair.flights.model.Interconnection;

import reactor.core.publisher.Flux;

public interface IFlight {

    Flux<Interconnection> getScheduledFlights(String arrival, String departure, LocalDateTime arrivalDateTime,
            LocalDateTime departureDateTime);
}
