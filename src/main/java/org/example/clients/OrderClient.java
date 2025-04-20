package org.example.clients;

import io.restassured.response.Response;
import org.example.models.CourierCredentials;
import org.example.models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String API_V1_ORDERS = "/api/v1/orders";
    public Response create(Order order){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(API_V1_ORDERS);
    }
}
