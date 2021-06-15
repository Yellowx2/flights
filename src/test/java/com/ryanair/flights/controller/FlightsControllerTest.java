package com.ryanair.flights.controller;

import com.ryanair.flights.model.Month;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.service.IFlight;
import com.ryanair.flights.service.IRyanairApi;
import com.ryanair.flights.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;

@WebFluxTest
class FlightsControllerTest {

    @MockBean
    private IRyanairApi api;

    @MockBean
    private IFlight flight;

    @Autowired
    private WebTestClient client;

    @Test
    void getRoutes() {

        var route1 = TestUtils.createRoute("DUB", "WRO");

        given(api.getRyanairRoutes()).willReturn(Flux.just(route1));

        client.get()
                .uri("/flights/route")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Route.class)
                .contains(route1);
    }

    @Test
    void getSchedules() {

        var month = TestUtils.createMonthlyFlights(LocalDateTime.now().getMonthValue());

        given(api.getScheduledFlights(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .willReturn(Mono.just(month));

        client.get()
                .uri(ub -> ub.path("/flights/schedule")
                .queryParam("departure", TestUtils.DUBLIN)
                .queryParam("arrival", TestUtils.WROCLAW)
                .queryParam("year", LocalDateTime.now().getYear())
                .queryParam("month", LocalDateTime.now().getMonthValue())
                .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Month.class)
                .isEqualTo(month);
    }

    @Test
    void getInterconnections() {
    }
}