import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.example.clients.OrderListClient;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class OrderListTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private OrderListClient orderList;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderList = new OrderListClient();
    }

    @Test
    @Step("Получение списка заказов")
    public void canGetOrderListSuccessfully() {
        Response getListResponse = orderList.get();
        assertEquals("Неверный код ответа", SC_OK, getListResponse.statusCode());
        assertNotNull("Нет списка заказов", getListResponse.jsonPath().getList("orders"));
        assertFalse("Пустой список заказов", getListResponse.jsonPath().getList("orders").isEmpty());
    }
}