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
import static stellarburgers.support.ApiAssertions.assertUser;

@DisplayName("Изменение данных пользователя")
class UpdateUserTest extends BaseApiTest {
    @Test
    @DisplayName("Изменённый с авторизацией email сохраняется в профиле")
    void testUpdateEmailWithAuthorization() {
        UserData user = createUser();
        String newEmail = "updated-" + UUID.randomUUID() + "@example.com";
        Response response = userClient.update(new UserData(newEmail, null, null));
        user.setEmail(newEmail);
        assertUser(response, user);
        assertUser(userClient.getUser(), user);
    }

    @Test
    @DisplayName("Изменённое с авторизацией имя сохраняется в профиле")
    void testUpdateNameWithAuthorization() {
        UserData user = createUser();
        String newName = "Новое имя";
        Response response = userClient.update(new UserData(null, null, newName));
        user.setName(newName);
        assertUser(response, user);
        assertUser(userClient.getUser(), user);
    }

    @Test
    @DisplayName("Изменённый с авторизацией пароль позволяет войти в систему")
    void testUpdatePasswordWithAuthorization() {
        UserData user = createUser();
        String newPassword = "Updated-" + UUID.randomUUID();
        Response response = userClient.update(new UserData(null, newPassword, null));
        user.setPassword(newPassword);
        assertUser(response, user);
        assertAuthentication(userClient.login(user), user);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ValueSource(strings = {"email", "name", "password"})
    @DisplayName("Изменение поля без авторизации возвращает ошибку")
    void testUpdateFieldWithoutAuthorization(String field) {
        UserData user = createUser();
        UserData changes = new UserData(null, null, null);
        String newValue = "updated-" + UUID.randomUUID();
        if (field.equals("email")) {
            newValue += "@example.com";
        }
        changes.setField(field, newValue);
        Response response = userClient.updateWithoutAuthorization(changes);
        assertError(response, HttpStatus.SC_UNAUTHORIZED, "You should be authorised");
        assertUser(userClient.getUser(), user);
        if (field.equals("password")) {
            assertAuthentication(userClient.login(user), user);
        }
    }
}
