package com.ryanair.flights.service;

import com.ryanair.flights.config.RyanairConfig;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.utils.CommonConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Service
public class RyanairApiService implements IRyanairApi {

    @Autowired
    private RyanairConfig config;

    @Override
    public Flux<Route> getRoutes() {

        return WebClient.create(config.getRoute().getUrl())
                .get()
                .uri(ub -> ub.path(config.getRoute().getPath()).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Route.class);
    }

    @Override
    public Flux<Route> getRyanairRoutes() {

        return getRoutes().filter(route -> route.getConnectingAirport() == null)
                .filter(route -> CommonConstants.RYANAIR.equals(route.getOperator()));
    }

}
