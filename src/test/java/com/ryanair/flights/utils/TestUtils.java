package com.ryanair.flights.utils;

import com.ryanair.flights.model.Day;
import com.ryanair.flights.model.Flight;
import com.ryanair.flights.model.Month;
import com.ryanair.flights.model.Route;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class TestUtils {

    public static final String RYANAIR = "RYANAIR";
    public static final String GENERIC = "GENERIC";
    public static final String DUBLIN = "DUB";
    public static final String WROCLAW = "WRO";
    public static final String CARRIER_CODE = "FR";
    public static final String FLIGHT_NUMBER = "1926";

    public static Route createRoute(String departureAirport, String arrivalAirport) {

        var route = new Route();
        route.setAirportFrom(departureAirport);
        route.setAirportTo(arrivalAirport);
        route.setNewRoute(false);
        route.setSeasonalRoute(false);
        route.setOperator(RYANAIR);
        route.setGroup(GENERIC);

        return route;
    }

    public static Month createMonthlyFlights(int month) {

        var monthlyFlights = new Month();

        monthlyFlights.setMonth(month);

        var days = new HashSet<Day>();
        days.add(createDailyFlights(LocalDateTime.now().getDayOfMonth()));

        monthlyFlights.setDays(days);

        return monthlyFlights;
    }

    public static Day createDailyFlights(int day) {

        var dailyFlights = new Day();

        dailyFlights.setDay(day);
        var flight = new Flight();
        flight.setCarrierCode(CARRIER_CODE);
        flight.setNumber(FLIGHT_NUMBER);
        flight.setDepartureTime(LocalTime.now().toString());
        flight.setArrivalTime(LocalTime.now().plusHours(3).toString());
        dailyFlights.setFlights(Set.of(flight));

        return dailyFlights;
    }
}
