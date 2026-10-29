package stellarburgers.tests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.client.IngredientClient;
import stellarburgers.client.OrderClient;

import java.util.Collections;
import java.util.List;

import static stellarburgers.support.ApiAssertions.assertCreatedOrder;
import static stellarburgers.support.ApiAssertions.assertError;
import static stellarburgers.support.ApiAssertions.assertInvalidIngredient;
import static stellarburgers.support.ApiAssertions.assertUnauthorizedOrder;

@DisplayName("Создание заказа")
class CreateOrderTest extends BaseApiTest {
    private final OrderClient orderClient = new OrderClient(userClient);
    private final IngredientClient ingredientClient = new IngredientClient();

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами возвращает номер заказа")
    void testCreateOrderWithAuthorization() {
        createUser();
        List<String> ingredients = ingredientClient.getIngredients();
        Response response = orderClient.create(ingredients, true);
        assertCreatedOrder(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации запрещено согласно документации")
    void testCreateOrderWithoutAuthorization() {
        List<String> ingredients = ingredientClient.getIngredients();
        Response response = orderClient.create(ingredients, false);
        assertUnauthorizedOrder(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает ошибку")
    void testCreateOrderWithoutIngredients() {
        createUser();
        Response response = orderClient.create(Collections.emptyList(), true);
        assertError(response, HttpStatus.SC_BAD_REQUEST, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента возвращает HTTP 500")
    void testCreateOrderWithInvalidIngredientHash() {
        createUser();
        Response response = orderClient.create(Collections.singletonList("invalid-ingredient-hash"), true);
        assertInvalidIngredient(response);
    }
}
