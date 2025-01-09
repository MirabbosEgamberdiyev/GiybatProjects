package api.giybat.uz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultAsync<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResultAsync<T> success(T data, String message) {
        ResultAsync<T> result = new ResultAsync<>();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> ResultAsync<T> success(T data) {
        return new ResultAsync<>(true, "Operation successful", data);
    }

    public static <T> ResultAsync<T> failure(String message) {
        return new ResultAsync<>(false, message, null);
    }
}
