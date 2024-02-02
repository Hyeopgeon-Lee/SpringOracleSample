package kopo.poly.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiResponse<T> {

    private HttpStatus httpStatus;
    private String message;

    private T data;

    @Builder
    public ApiResponse(HttpStatus httpStatus, String message, T data) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data);
    }
}
