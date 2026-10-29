package stellarburgers.support;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;
import stellarburgers.model.UserData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class ApiAssertions {
    @Step("Проверить HTTP-статус и признак успешного ответа")
    public static void assertSuccess(Response response, int expectedStatus) {
        assertThat(response.statusCode()).as("HTTP-статус ответа").isEqualTo(expectedStatus);
        assertThat(response.jsonPath().getBoolean("success")).as("Признак успешного ответа").isTrue();
    }

    @Step("Проверить HTTP-статус и тело ошибки")
    public static void assertError(Response response, int expectedStatus, String expectedMessage) {
        assertThat(response.statusCode()).as("HTTP-статус ошибки").isEqualTo(expectedStatus);
        assertThat(response.jsonPath().getBoolean("success")).as("Признак неуспешного ответа").isFalse();
        assertThat(response.jsonPath().getString("message")).as("Сообщение об ошибке").isEqualTo(expectedMessage);
    }

    @Step("Проверить успешный ответ и данные пользователя")
    public static void assertUser(Response response, UserData expectedUser) {
        assertSuccess(response, HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("user.email")).as("Email пользователя").isEqualTo(expectedUser.getEmail());
        assertThat(response.jsonPath().getString("user.name")).as("Имя пользователя").isEqualTo(expectedUser.getName());
    }

    @Step("Проверить успешную авторизацию, данные пользователя и наличие токенов")
    public static void assertAuthentication(Response response, UserData expectedUser) {
        assertUser(response, expectedUser);
        String accessToken = response.jsonPath().getString("accessToken");
        String refreshToken = response.jsonPath().getString("refreshToken");
        // Проверяем условия, чтобы значения токенов не попали в сообщение AssertJ при ошибке.
        assertThat(accessToken != null && accessToken.startsWith("Bearer ") && accessToken.length() > 7)
                .as("Ответ содержит непустой accessToken с префиксом Bearer").isTrue();
        assertThat(refreshToken != null && !refreshToken.isBlank())
                .as("Ответ содержит непустой refreshToken").isTrue();
    }

    @Step("Проверить успешное создание заказа и его номер")
    public static int assertCreatedOrder(Response response) {
        assertSuccess(response, HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("name")).as("Название заказа").isNotBlank();
        Integer number = response.jsonPath().getObject("order.number", Integer.class);
        assertThat(number).as("Номер созданного заказа").isNotNull().isPositive();
        return number;
    }

    @Step("Проверить наличие созданного заказа в заказах пользователя")
    public static void assertContainsOrder(Response response, int orderNumber, List<String> expectedIngredients) {
        assertSuccess(response, HttpStatus.SC_OK);
        List<Integer> numbers = response.jsonPath().getList("orders.number", Integer.class);
        assertThat(numbers).as("Номера заказов пользователя").contains(orderNumber);
        List<String> ingredients = response.jsonPath()
                .getList("orders.find { it.number == " + orderNumber + " }.ingredients", String.class);
        assertThat(ingredients).as("Ингредиенты сохранённого заказа")
                .containsExactlyInAnyOrderElementsOf(expectedIngredients);
    }

    @Step("Проверить запрет создания заказа без авторизации согласно странице 5 PDF")
    public static void assertUnauthorizedOrder(Response response) {
        // тест падает потому что нет валидации на авторизацию, ожидаем 401 без авторизации, но ответ 200
        assertThat(response.statusCode()).as("Создание заказа требует авторизации (PDF, стр. 5)")
                .isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        assertThat(response.jsonPath().getBoolean("success")).as("Заказ не создан").isFalse();
        // В PDF нет точного текста ошибки для POST /api/orders без авторизации.
        assertThat(response.jsonPath().getString("message")).as("Объяснение отказа в создании заказа").isNotBlank();
    }

    @Step("Проверить HTTP 500 и наличие тела ответа для неверного хеша")
    public static void assertInvalidIngredient(Response response) {
        assertThat(response.statusCode()).as("HTTP-статус при неверном хеше ингредиента")
                .isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        // Документация задаёт только статус; формат и текст тела ошибки не определены.
        assertThat(response.asString()).as("Тело ответа при неверном хеше ингредиента").isNotBlank();
    }
}
