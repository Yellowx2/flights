package com.ryanair.flights.service;

import com.ryanair.flights.model.Month;
import com.ryanair.flights.model.Route;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface that represents all external API calls made through the application
 */
public interface IRyanairApi {

    /**
     * Obtains all routes from https://services-api.ryanair.com
     *
     * @return Flux collection with all routes
     */
    Flux<Route> getRoutes();

    /**
     * Filters routes obtained by getRoutes without connecting airports and only
     * operated by Ryanair
     *
     * @return Filtered routes
     */
    Flux<Route> getRyanairRoutes();

    /**
     * Obtains a list of the flights available in the month indi8cated
     *
     * @param departure Departure airport code
     * @param arrival   Arrival airport code
     * @param year      Year of the departure
     * @param month     Month of the departure
     * @return List of flights
     */
    Mono<Month> getScheduledFlights(String departure, String arrival, int year, int month);
}
