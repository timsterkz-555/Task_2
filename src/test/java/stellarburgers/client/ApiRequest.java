package stellarburgers.client;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.given;

@UtilityClass
public class ApiRequest {
    public static RequestSpecification request() {
        return given()
                .baseUri("https://qa-stellarburgers.education-services.ru")
                .contentType(ContentType.JSON)
                .config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 15000)
                        .setParam("http.socket.timeout", 30000)));
    }

    public static RequestSpecification authorizedRequest(String accessToken) {
        return request().header("Authorization", accessToken);
    }
}
