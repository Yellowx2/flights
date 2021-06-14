package com.ryanair.flights.service;

import com.ryanair.flights.model.Month;
import com.ryanair.flights.model.Route;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface that represents all external API calls made through the application
 */
public interface IRyanairApi {

    Flux<Route> getRoutes();

    Flux<Route> getRyanairRoutes();

    Mono<Month> getScheduledFlights(String departure, String arrival, int year, int month);
}
