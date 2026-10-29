package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import stellarburgers.model.UserData;

public class UserClient {
    private String accessToken;

    @Step("Зарегистрировать пользователя")
    public Response register(UserData user) {
        Response response = ApiRequest.request().body(user).post("/api/auth/register");
        // Сохраняем токен до любых проверок, чтобы @AfterEach смог удалить пользователя.
        if (response.contentType().contains("json")) {
            String token = response.jsonPath().getString("accessToken");
            if (token != null && !token.isBlank()) {
                accessToken = token;
            }
        }
        return response;
    }

    @Step("Войти в систему")
    public Response login(UserData user) {
        UserData credentials = new UserData(user.getEmail(), user.getPassword(), null);
        return ApiRequest.request().body(credentials).post("/api/auth/login");
    }

    @Step("Изменить данные пользователя с авторизацией")
    public Response update(UserData changes) {
        return ApiRequest.authorizedRequest(accessToken).body(changes).patch("/api/auth/user");
    }

    @Step("Изменить данные пользователя без авторизации")
    public Response updateWithoutAuthorization(UserData changes) {
        return ApiRequest.request().body(changes).patch("/api/auth/user");
    }

    @Step("Получить сохранённые данные пользователя")
    public Response getUser() {
        return ApiRequest.authorizedRequest(accessToken).get("/api/auth/user");
    }

    @Step("Удалить созданного пользователя")
    public Response delete() {
        return ApiRequest.authorizedRequest(accessToken).delete("/api/auth/user");
    }

    public boolean hasCreatedUser() {
        return accessToken != null;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
