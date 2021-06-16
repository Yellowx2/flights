package com.ryanair.flights.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for Ryanair routes API
 */
@Data
@NoArgsConstructor
public final class Route {

    private String airportFrom;
    private String airportTo;
    private String connectingAirport;
    private boolean newRoute;
    private boolean seasonalRoute;
    private String operator;
    private String group;
}
