package com.ryanair.flights.model;

import java.util.Set;

import lombok.Data;

@Data
public class Interconnection {

    private int stops;
    private Set<Leg> legs;
}
