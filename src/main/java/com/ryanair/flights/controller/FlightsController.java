package com.ryanair.flights.controller;

import com.ryanair.flights.model.Month;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.service.IRyanairApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for Flight application
 */
@RestController
@RequestMapping(value = "/flights")
public class FlightsController {

    @Autowired
    private IRyanairApi api;

    /**
     * This method is only for testing purposes. It should not be in production-ready code but I'll leave it as part of my local testing process
     *
     * @return Routes API result
     */
    @GetMapping(value = "/route")
    public Flux<Route> getRoutes() {
        return api.getRyanairRoutes();
    }

    /**
     * This method is only for testing purposes. It should not be in production-ready code but I'll leave it as part of my local testing process
     *
     * @param departure Departure airport
     * @param arrival   Arrival airport
     * @param year      Year of departure
     * @param month     Month of departure
     * @return Schedule API results
     */
    @GetMapping(value = "/schedule")
    public Mono<Month> getSchedules(@RequestParam String departure,
                                    @RequestParam String arrival,
                                    @RequestParam int year,
                                    @RequestParam int month) {

        return api.getScheduledFlights(departure, arrival, year, month);
    }

}
