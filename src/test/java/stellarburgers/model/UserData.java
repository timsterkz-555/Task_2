package stellarburgers.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {
    private String email;
    private String password;
    private String name;

    public static UserData unique() {
        return new UserData("student-" + UUID.randomUUID() + "@example.com",
                "Password-" + UUID.randomUUID(), "Студент");
    }

    public void setField(String field, String value) {
        switch (field) {
            case "email":
                email = value;
                break;
            case "password":
                password = value;
                break;
            case "name":
                name = value;
                break;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field);
        }
    }

    // Не добавляем toString(): Allure сохраняет строковое представление аргументов шагов.
}
