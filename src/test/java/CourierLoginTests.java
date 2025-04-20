import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.clients.CourierClient;
import org.example.models.Courier;
import org.example.models.CourierCredentials;
import org.example.models.CourierLoginResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.apache.http.HttpStatus.*;

import static org.example.generators.CourierGenerator.randomCourier;
import static org.example.models.CourierCredentials.credentialsFromCourier;

public class CourierLoginTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private CourierClient courierClient;
    private int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        courierClient = new CourierClient();
    }

    @Test
    @Step("Курьер может авторизоваться")
    public void courierIsAbleToLogin(){
        Courier courier = randomCourier();
        courierClient.create(courier);
        CourierCredentials courierCredentials = credentialsFromCourier(courier);
        Response response = courierClient.login(courierCredentials);
        id = response.as(CourierLoginResponse.class).getId();

        assertEquals("Некорректный код ответа при успешной авторизации", SC_OK, response.statusCode());
        assertEquals("Некорректный ответ при успешной авторизации", id, response.getBody().jsonPath().getInt("id") );
    }

    @Test
    @Step("Чтобы авторизоваться, нужно передать в ручку пароль")
    public void requiredPasswordToLogin(){
        Courier courier = new Courier().setLogin("Login1");
        CourierCredentials courierCredentials = credentialsFromCourier(courier);
        Response response = courierClient.login(courierCredentials);

        assertEquals("Некорректный код ответа при отсутствии пароля в запросе", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Некорректный ответ при отсутствии пароля в запросе", "Недостаточно данных для входа", response.getBody().jsonPath().getString("message") );
    }

    @Test
    @Step("Чтобы авторизоваться, нужно передать в ручку логин")
    public void requiredLoginToLogin(){
        Courier courier = new Courier().setPassword("Password1");
        CourierCredentials courierCredentials = credentialsFromCourier(courier);
        Response response = courierClient.login(courierCredentials);

        assertEquals("Некорректный код ответа при отсутствии логина в запросе", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Некорректный ответ при отсутствии логина в запросе", "Недостаточно данных для входа", response.getBody().jsonPath().getString("message") );
    }

    @Test
    @Step("Система вернёт ошибку, если неправильно указать логин")
    public void errorReturnedWrongLogin(){
        Courier courier = randomCourier();
        courierClient.create(courier);

        CourierCredentials wrongCredentials = new CourierCredentials("wrongLogin", courier.getPassword());
        Response response = courierClient.login(wrongCredentials);


        assertEquals("Некорректный код ответа при неправильном логине", SC_NOT_FOUND, response.statusCode());
        assertEquals("Некорректный ответ при неправильном логине", "Учетная запись не найдена", response.getBody().jsonPath().getString("message") );

        Response loginResponse = courierClient.login(credentialsFromCourier(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }

    @Test
    @Step("Система вернёт ошибку, если неправильно указать пароль")
    public void errorReturnedWrongPassword(){
        Courier courier = randomCourier();
        courierClient.create(courier);

        CourierCredentials wrongCredentials = new CourierCredentials(courier.getLogin(), "wrongPassword");
        Response response = courierClient.login(wrongCredentials);


        assertEquals("Некорректный код ответа при неправильном пароле", SC_NOT_FOUND, response.statusCode());
        assertEquals("Некорректный ответ при неправильном пароле", "Учетная запись не найдена", response.getBody().jsonPath().getString("message") );

        Response loginResponse = courierClient.login(credentialsFromCourier(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }

    @Test
    @Step("если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void errorReturnedUserDoesNotExist(){
        Courier courier = randomCourier();
        Response response = courierClient.login(credentialsFromCourier(courier));


        assertEquals("Некорректный код ответа при несуществующем пользователе", SC_NOT_FOUND, response.statusCode());
        assertEquals("Некорректный ответ при ннесуществующем пользователе", "Учетная запись не найдена", response.getBody().jsonPath().getString("message") );

    }

    @After
    public void tearTest() {
        courierClient.delete(id);
    }
}
