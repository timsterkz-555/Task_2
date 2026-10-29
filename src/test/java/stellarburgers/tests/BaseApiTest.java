package stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import stellarburgers.client.UserClient;
import stellarburgers.model.UserData;

import static stellarburgers.support.ApiAssertions.assertAuthentication;
import static stellarburgers.support.ApiAssertions.assertSuccess;

public abstract class BaseApiTest {
    protected final UserClient userClient = new UserClient();

    @Step("Подготовить уникального зарегистрированного пользователя")
    protected UserData createUser() {
        UserData user = UserData.unique();
        Response response = userClient.register(user);
        assertAuthentication(response, user);
        return user;
    }

    @AfterEach
    @Step("Очистить созданного в тесте пользователя")
    void deleteUser() {
        if (userClient.hasCreatedUser()) {
            Response response = userClient.delete();
            assertSuccess(response, HttpStatus.SC_ACCEPTED);
        }
    }
}
