package stellarburgers.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stellarburgers.client.IngredientClient;
import stellarburgers.client.OrderClient;

import java.util.List;

import static stellarburgers.support.ApiAssertions.assertContainsOrder;
import static stellarburgers.support.ApiAssertions.assertCreatedOrder;
import static stellarburgers.support.ApiAssertions.assertError;

@DisplayName("Получение заказов пользователя")
class GetUserOrdersTest extends BaseApiTest {
    private final OrderClient orderClient = new OrderClient(userClient);
    private final IngredientClient ingredientClient = new IngredientClient();

    @Test
    @DisplayName("Авторизованный пользователь получает свой созданный заказ")
    void testGetOrdersWithAuthorization() {
        createUser();
        List<String> ingredients = ingredientClient.getIngredients();
        int orderNumber = createOrder(ingredients);
        Response response = orderClient.getOrders(true);
        assertContainsOrder(response, orderNumber, ingredients);
    }

    @Test
    @DisplayName("Получение заказов без авторизации возвращает ошибку")
    void testGetOrdersWithoutAuthorization() {
        Response response = orderClient.getOrders(false);
        assertError(response, HttpStatus.SC_UNAUTHORIZED, "You should be authorised");
    }

    @Step("Подготовить заказ для проверки списка заказов пользователя")
    private int createOrder(List<String> ingredients) {
        return assertCreatedOrder(orderClient.create(ingredients, true));
    }
}
