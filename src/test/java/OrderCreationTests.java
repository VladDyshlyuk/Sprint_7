import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.clients.OrderClient;
import org.example.models.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class OrderCreationTests {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private final List<String> color;
    private OrderClient orderClient;

    public OrderCreationTests(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderClient = new OrderClient();
    }

    @Test
    @Step("Создание заказа с цветом/цветами: {0}")
    public void testCreateOrderWithVariousColors() {
        Order order = new Order(color);
        Response response = orderClient.create(order);

        assertEquals("Некорректный код ответа", SC_CREATED, response.statusCode());
        assertNotNull("Некорректный ответ", response.jsonPath().get("track"));
    }

}
