package com.ryanair.flights.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.ryanair.flights.model.Flight;
import com.ryanair.flights.model.Interconnection;
import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.utils.CommonConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightService implements IFlight {

    @Autowired
    private IRyanairApi api;

    @Override
    public Set<Interconnection> getScheduledFlights(String arrivalAirport, String departureAirport, LocalDateTime arrivalDateTime,
                                                    LocalDateTime departureDateTime) {

        // Get a list of the routes filtered by departure and arrival airports
        var routes = api.getRyanairRoutes()
                .filter(route -> departureAirport.equals(route.getAirportFrom())
                        || arrivalAirport.equals(route.getAirportTo()));

        Set<Interconnection> interconnections = new HashSet<>();

        // Iterate through the routes
        routes.subscribe(route -> {

            // Process direct flights
            if (isDirectFlight(route, departureAirport, arrivalAirport)) {
                addDirectFlights(arrivalDateTime, departureDateTime, interconnections, route);
            } else {
                addOneStopFlights(arrivalDateTime, departureDateTime, interconnections, route);
            }
        });

        return interconnections;
    }

    private void addDirectFlights(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime, Set<Interconnection> interconnections, Route route) {

        // API call to obtain flights from a specific month. It should be done separately
        var monthlyFlights = api.getScheduledFlights(route.getAirportFrom(), route.getAirportTo(), departureDateTime.getYear(),
                departureDateTime.getMonthValue());

        // Days will be filtered between arrival day and departure day and processed after that
        monthlyFlights.subscribe(month -> {

            var dailyFlights = month.getDays().stream()
                    .filter(day -> day.getDay() >= departureDateTime.getDayOfMonth()
                            && day.getDay() <= arrivalDateTime.getDayOfMonth())
                    .collect(Collectors.toSet());

            // month.getDays().stream()
            //     .filter(day -> day.getDay() >= departureDateTime.getDayOfMonth()
            //             && day.getDay() <= arrivalDateTime.getDayOfMonth())
            //     .collect(Collectors.toSet())
                dailyFlights.forEach(day -> {

                    var legs = new HashSet<Leg>();

                    // Add every flight from each day
                    var flights = day.getFlights();
                    /* day.getFlights() */flights.forEach(flight -> {

                        addLeg(route, legs,
                                LocalDateTime.of(LocalDate.of(departureDateTime.getYear(),
                                        month.getMonth(), day.getDay()), LocalTime.parse(flight.getDepartureTime())),
                                LocalDateTime.of(LocalDate.of(arrivalDateTime.getYear(),
                                        month.getMonth(), day.getDay()), LocalTime.parse(flight.getArrivalTime())),
                                flight);
                    });

                    addInterconnections(interconnections, legs);
                });
        });
    }

    private void addOneStopFlights(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime, Set<Interconnection> interconnections, Route route) {
        //TODO
    }

    /**
     * Checks if a flight is direct
     * @param route Route flight obtained through the API
     * @param arrivalAirport Destination airport
     * @param departureAirport Origin airport
     * @return True if the flight is a direct one
     */
    private boolean isDirectFlight(Route route, String departureAirport, String arrivalAirport) {
        return route.getAirportFrom().equals(departureAirport) && route.getAirportTo().equals(arrivalAirport);
    }

    /**
     * Creates a leg
     * @param route
     * @param legs
     * @param departureDateTime
     * @param arrivalDateTime
     * @param flight
     */
    private void addLeg(Route route, Set<Leg> legs, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Flight flight) {

        var leg = new Leg();
        leg.setDepartureAirport(route.getAirportFrom());
        leg.setArrivalAirport(route.getAirportTo());
        leg.setDepartureDateTime(departureDateTime);
        leg.setArrivalDateTime(arrivalDateTime);

        legs.add(leg);
    }

    /**
     * Adds flights to the set only if legs aren't empty
     * @param interconnections Response set
     * @param legs
     */
    private void addInterconnections(Set<Interconnection> interconnections, HashSet<Leg> legs) {

        if (!legs.isEmpty()) {
            var interconnection = new Interconnection();
            interconnection.setLegs(legs);
            interconnection.setStops(CommonConstants.DIRECT_FLIGHT);

            interconnections.add(interconnection);
        }
    }

}
