package com.ryanair.flights.model;

import java.util.Set;

import lombok.Data;

/**
 * Model for schedule API that groups flights in a specific day of a month
 */
@Data
public class Day {

    private int day;
    private Set<Flight> flights;
}
