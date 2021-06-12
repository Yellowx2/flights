package com.ryanair.flights.service;

import com.ryanair.flights.model.Route;

import reactor.core.publisher.Flux;

public interface IRyanairApi {

    Flux<Route> getRoutes();

    Flux<Route> getRyanairRoutes();
}
