package org.example.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderListClient {

    private static final String API_V1_ORDERS = "/api/v1/orders";

    public Response get() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(API_V1_ORDERS);
    }
}