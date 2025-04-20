import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.clients.CourierClient;
import org.example.models.Courier;
import org.example.models.CourierLoginResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.example.generators.CourierGenerator.randomCourier;
import static org.example.models.CourierCredentials.credentialsFromCourier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourierCreationTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private CourierClient courierClient;
    private int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        courierClient = new CourierClient();
    }

    @Test
    @Step("Курьера можно создать")
    public void createCourierTest(){
        Courier courier = randomCourier();
        Response response = courierClient.create(courier);
        assertEquals("Некорректный код ответа", SC_CREATED, response.statusCode());
        assertTrue("Некорректное сообщение при успешном запросе", response.jsonPath().getBoolean("ok"));

        Response loginResponse = courierClient.login(credentialsFromCourier(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }

    @Test
    @Step("Нельзя создать двух одинаковых курьеров")
    public void unableToCreateIdenticalCouriersTest(){
        Courier courier = randomCourier();
        Response response = courierClient.create(courier);
        Response secondResponse = courierClient.create(courier);

        assertEquals("Некорректный код ответа при создании двух одинаковых курьеров", SC_CONFLICT, secondResponse.statusCode());
        assertEquals("Некорректное сообщение при создании двух одинаковых курьеров", "Этот логин уже используется. Попробуйте другой.", secondResponse.jsonPath().getString("message"));


        Response loginResponse = courierClient.login(credentialsFromCourier(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }

    @Test
    @Step("чтобы создать курьера, нужно передать в ручку логин")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier()
                .setPassword("1234")
                .setFirstName("TestName");

        Response response = courierClient.create(courier);

        assertEquals("Некорректный код ответа при отсутствии логина", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Некорректное сообщение при отсутствии логина", "Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }
    @Test
    @Step("чтобы создать курьера, нужно передать в ручку пароль")
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier()
                .setLogin("test_login")
                .setFirstName("TestName");

        Response response = courierClient.create(courier);

        assertEquals("Некорректный код ответа при отсутствии пароля", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Некорректное сообщение при отсутствии пароля", "Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }
    @Test
    @Step("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void cannotCreateCourierWithSameLogin() {
        Courier courier1 = new Courier()
                .setLogin("Login123")
                .setPassword("Password123")
                .setFirstName("Courier1");

        courierClient.create(courier1);

        Courier courier2 = new Courier()
                .setLogin("Login123")
                .setPassword("pass123")
                .setFirstName("Courier2");

        Response response = courierClient.create(courier2);

        assertEquals("Некорректный код ответа при создании курьера с тем же логином", SC_CONFLICT, response.statusCode());
        assertEquals("Некорректное сообщение при создании курьера с тем же логином ", "Этот логин уже используется. Попробуйте другой.", response.jsonPath().getString("message"));


        Response loginResponse = courierClient.login(credentialsFromCourier(courier1));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }




    @After
    public void tearTest() {
        courierClient.delete(id);
    }
}
