package com.ryanair.flights.model;

import java.util.Set;

import lombok.Data;

/**
 * Model for schedule API that groups flights per day
 */
@Data
public class Month {

    private int month;
    private Set<Day> days;
}
