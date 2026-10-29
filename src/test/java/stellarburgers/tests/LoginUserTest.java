package stellarburgers.tests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import stellarburgers.model.UserData;

import java.util.UUID;

import static stellarburgers.support.ApiAssertions.assertAuthentication;
import static stellarburgers.support.ApiAssertions.assertError;

@DisplayName("Вход пользователя")
class LoginUserTest extends BaseApiTest {
    @Test
    @DisplayName("Вход существующего пользователя возвращает его данные и токены")
    void testLoginExistingUser() {
        UserData user = createUser();
        Response response = userClient.login(user);
        assertAuthentication(response, user);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ValueSource(strings = {"email", "password"})
    @DisplayName("Вход с неверным email или паролем возвращает ошибку")
    void testLoginWithIncorrectCredentials(String field) {
        UserData user = createUser();
        String incorrectValue = field.equals("email")
                ? "unknown-" + UUID.randomUUID() + "@example.com"
                : "Incorrect-" + UUID.randomUUID();
        user.setField(field, incorrectValue);
        Response response = userClient.login(user);
        assertError(response, HttpStatus.SC_UNAUTHORIZED, "email or password are incorrect");
    }
}
