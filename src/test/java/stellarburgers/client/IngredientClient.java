package stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static stellarburgers.support.ApiAssertions.assertSuccess;

public class IngredientClient {
    @Step("Получить актуальные ингредиенты и выбрать булку и начинку")
    public List<String> getIngredients() {
        Response response = ApiRequest.request().get("/api/ingredients");
        assertSuccess(response, HttpStatus.SC_OK);
        List<String> buns = response.jsonPath().getList("data.findAll { it.type == 'bun' }._id", String.class);
        List<String> mains = response.jsonPath().getList("data.findAll { it.type == 'main' }._id", String.class);
        assertThat(buns).as("В каталоге есть булки с идентификаторами").isNotEmpty().doesNotContainNull();
        assertThat(mains).as("В каталоге есть начинки с идентификаторами").isNotEmpty().doesNotContainNull();
        return Arrays.asList(buns.get(0), mains.get(0));
    }
}
