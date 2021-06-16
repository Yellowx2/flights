package com.ryanair.flights.service;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.flights.config.RyanairConfig;
import com.ryanair.flights.model.UrlDto;
import com.ryanair.flights.utils.TestUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RyanairApiServiceTest {

    private static final String SCHEDULES_PATH = "/3/schedules/{departure}/{arrival}/years/{year}/months/{month}";

    private static final String ROUTES_PATH = "/locate/3/routes";

    @Mock
    private RyanairConfig config;

    @InjectMocks
    private static RyanairApiService service;

    private static MockWebServer server;

    private static ObjectMapper mapper;

    private static UrlDto url;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        mapper = new ObjectMapper();
        service = new RyanairApiService();
        url = new UrlDto();
        url.setUrl(server.url("").toString());
    }

    @AfterAll
    static void teardown() throws IOException {
        server.shutdown();
    }

    @Test
    void getRoutesTest() throws JsonProcessingException {

        var route = TestUtils.createRoute(TestUtils.DUBLIN, TestUtils.WROCLAW);

        url.setPath(ROUTES_PATH);
        when(config.getRoute()).thenReturn(url);

        server.enqueue(new MockResponse()
                .addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(route)));

        var response = service.getRoutes();

        StepVerifier.create(response)
                .expectNext(route)
                .verifyComplete();
    }

    @Test
    void getRyanairRoutesTest() throws JsonProcessingException {

        var route = TestUtils.createRoute(TestUtils.DUBLIN, TestUtils.WROCLAW);
        var filteredRoute1 = TestUtils.createRoute(TestUtils.DUBLIN, TestUtils.WROCLAW, TestUtils.IBERIA, null);
        var filteredRoute2 = TestUtils.createRoute(TestUtils.DUBLIN, TestUtils.WROCLAW, TestUtils.RYANAIR, TestUtils.DUBLIN);

        url.setPath(ROUTES_PATH);
        when(config.getRoute()).thenReturn(url);

        server.enqueue(new MockResponse()
                .addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(Set.of(route, filteredRoute1, filteredRoute2))));

        var response = service.getRyanairRoutes();

        StepVerifier.create(response)
                .expectNext(route)
                .verifyComplete();
    }

    @Test
    void getScheduledFlightsTest() throws JsonProcessingException {

        var monthlyFlights = TestUtils.createMonthlyFlights(LocalDateTime.now().getMonthValue());

        url.setPath(SCHEDULES_PATH);
        when(config.getSchedule()).thenReturn(url);

        server.enqueue(new MockResponse()
                .addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(monthlyFlights)));

        var response = service.getScheduledFlights(TestUtils.DUBLIN, TestUtils.WROCLAW, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());

        StepVerifier.create(response)
                .expectNext(monthlyFlights)
                .verifyComplete();
    }
}