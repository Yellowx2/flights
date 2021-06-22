package com.ryanair.flights.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.ryanair.flights.model.Day;
import com.ryanair.flights.model.Flight;
import com.ryanair.flights.model.Interconnection;
import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Month;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.utils.CommonConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class FlightService implements IFlight {

    @Autowired
    private IRyanairApi api;

    @Override
    public Flux<Interconnection> getScheduledFlights(String arrivalAirport, String departureAirport, LocalDateTime arrivalDateTime,
                                                    LocalDateTime departureDateTime) {

        // Get a list of the routes filtered by departure and arrival airports
        Flux<Route> routes = api.getRyanairRoutes();
                // .filter(route -> departureAirport.equals(route.getAirportFrom())
                //         || arrivalAirport.equals(route.getAirportTo()));

        // Process direct flights
        Flux<Interconnection> directFlights = routes
                .subscribeOn(Schedulers.boundedElastic())
                .filter(route -> isDirectFlight(route, departureAirport, arrivalAirport))
                .flatMap(route -> addDirectFlights(arrivalDateTime, departureDateTime, route));

        // Process one stop flights
        Flux<Interconnection> oneStopFlights = routes
                .subscribeOn(Schedulers.boundedElastic())
                .filter(route -> !isDirectFlight(route, departureAirport, arrivalAirport))
                .filter(route -> departureAirport.equals(route.getAirportFrom())
                        || arrivalAirport.equals(route.getAirportTo()))
                .flatMap(route -> addOneStopFlights(arrivalDateTime, departureDateTime, arrivalAirport, departureAirport, route, routes));

        return directFlights.concatWith(oneStopFlights);
    }

    private Flux<Interconnection> addDirectFlights(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime, Route route) {

        // API call to obtain flights from a specific month. It should be done separately
        var monthlyFlights = api.getScheduledFlights(route.getAirportFrom(), route.getAirportTo(), departureDateTime.getYear(),
                departureDateTime.getMonthValue());

        // Days will be filtered between arrival day and departure day and processed after that
        return monthlyFlights.subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(month -> Flux.fromStream(month.getDays().stream()
                        .filter(day -> day.getDay() >= departureDateTime.getDayOfMonth()
                                && day.getDay() <= arrivalDateTime.getDayOfMonth())
                        .map(day -> processDirectFlightsDay(arrivalDateTime, departureDateTime, route, month, day))));
    }

    /**
     * Processes direct flights in a day
     *
     * @param arrivalDateTime
     * @param departureDateTime
     * @param route
     * @param month
     * @param day
     * @return
     */
    private Interconnection processDirectFlightsDay(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime, Route route, Month month,
            Day day) {

        var legs = new HashSet<Leg>();

        // Add every flight from each day
        day.getFlights().forEach(flight -> {

            addLeg(route, legs,
                    LocalDateTime.of(LocalDate.of(departureDateTime.getYear(),
                            month.getMonth(), day.getDay()), LocalTime.parse(flight.getDepartureTime())),
                    LocalDateTime.of(LocalDate.of(arrivalDateTime.getYear(),
                            month.getMonth(), day.getDay()), LocalTime.parse(flight.getArrivalTime())),
                    flight);
        });

        if (!legs.isEmpty()) {
            return addInterconnections(legs);
        } else {
            return null;
        }
    }

    private Flux<Interconnection> addOneStopFlights(LocalDateTime arrivalDateTime, LocalDateTime departureDateTime, String arrivalAirport, String departureAirport, Route route, Flux<Route> routes) {
        //TODO
        Mono<Month> firstLegFlights;
        Mono<Month> secondLegFlights; // TODO: transform to day?

        // Checks if the route is first or second leg
        if (route.getAirportFrom().equals(departureAirport)) {
            firstLegFlights = api.getScheduledFlights(departureAirport, route.getAirportTo(), departureDateTime.getYear(),
                departureDateTime.getMonthValue());
            secondLegFlights = api.getScheduledFlights(route.getAirportTo(), arrivalAirport, departureDateTime.getYear(),
                departureDateTime.getMonthValue()); // TODO: filter?
        } else {
            firstLegFlights = api.getScheduledFlights(departureAirport, route.getAirportFrom(), departureDateTime.getYear(),
                departureDateTime.getMonthValue());
            secondLegFlights = api.getScheduledFlights(route.getAirportFrom(), arrivalAirport, departureDateTime.getYear(),
                departureDateTime.getMonthValue());
        }

        // Transforms and filters the first leg flights into a Flux of interconnections
        return firstLegFlights.flatMapMany(month -> Flux.fromStream(month.getDays().stream()
                .filter(day -> day.getDay() >= departureDateTime.getDayOfMonth()
                        && day.getDay() <= arrivalDateTime.getDayOfMonth())
                .flatMap(day -> processOneStopFlights(secondLegFlights, arrivalDateTime, departureDateTime, route, month, day))
        ));
    }

    private Stream<Interconnection> processOneStopFlights(Mono<Month> secondLegFlights, LocalDateTime arrivalDateTime,
            LocalDateTime departureDateTime, Route route, Month month, Day day) {

        //TODO
        day.getFlights().stream()
                .flatMap(f -> {
                    // secondLegFlights.filter(tester)
                    return null;
                });

        // secondLegFlights.flatMapMany(m -> m.getDays());
        return null;
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
     * Adds flights to the set
     *
     * @param interconnections Response set
     * @param legs
     */
    private Interconnection addInterconnections(HashSet<Leg> legs) {

        var interconnection = new Interconnection();
        interconnection.setLegs(legs);
        interconnection.setStops(CommonConstants.DIRECT_FLIGHT);

        return interconnection;
    }

}
