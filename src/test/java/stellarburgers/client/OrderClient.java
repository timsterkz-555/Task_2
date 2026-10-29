package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.List;

public class OrderClient {
    private final UserClient userClient;

    public OrderClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Step("Создать заказ с ингредиентами; авторизация: {authorized}")
    public Response create(List<String> ingredients, boolean authorized) {
        return request(authorized)
                .body(Collections.singletonMap("ingredients", ingredients))
                .post("/api/orders");
    }

    @Step("Получить заказы пользователя; авторизация: {authorized}")
    public Response getOrders(boolean authorized) {
        return request(authorized).get("/api/orders");
    }

    private RequestSpecification request(boolean authorized) {
        return authorized
                ? ApiRequest.authorizedRequest(userClient.getAccessToken())
                : ApiRequest.request();
    }
}
