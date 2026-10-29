package stellarburgers.tests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import stellarburgers.model.UserData;

import static stellarburgers.support.ApiAssertions.assertAuthentication;
import static stellarburgers.support.ApiAssertions.assertError;

@DisplayName("Создание пользователя")
class CreateUserTest extends BaseApiTest {
    @Test
    @DisplayName("Создание уникального пользователя возвращает его данные и токены")
    void testCreateUniqueUser() {
        UserData user = UserData.unique();
        Response response = userClient.register(user);
        assertAuthentication(response, user);
    }

    @Test
    @DisplayName("Повторная регистрация пользователя возвращает ошибку")
    void testCreateAlreadyRegisteredUser() {
        UserData user = createUser();
        Response response = userClient.register(user);
        assertError(response, HttpStatus.SC_FORBIDDEN, "User already exists");
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ValueSource(strings = {"email", "password", "name"})
    @DisplayName("Регистрация без обязательного поля возвращает ошибку")
    void testCreateUserWithoutRequiredField(String field) {
        UserData user = UserData.unique();
        user.setField(field, null);
        Response response = userClient.register(user);
        assertError(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }
}
