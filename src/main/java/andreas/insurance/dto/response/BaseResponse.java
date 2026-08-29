package andreas.insurance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BaseResponse<T>(
        MetaResponse meta,
        T data,
        ErrorResponse<?> error
) {

    public static <T> BaseResponse<T> success(T data, String message) {
        MetaResponse meta = MetaResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .correlationId(MDC.get("correlationId"))
                .message(message)
                .build();

        return new BaseResponse<>(meta, data, null);
    }

    public static <T> BaseResponse<T> error(String message, HttpStatus status) {
        MetaResponse meta = MetaResponse.builder()
                .success(false)
                .code(status.value())
                .correlationId(MDC.get("correlationId"))
                .message(message)
                .build();

        return new BaseResponse<>(meta, null, null);
    }

    public static <T, E> BaseResponse<T> errorWithDetails(String message, HttpStatus status, String errorType, E errorDetails) {
        MetaResponse meta = MetaResponse.builder()
                .success(false)
                .code(status.value())
                .correlationId(MDC.get("correlationId"))
                .message(message)
                .build();

        ErrorResponse<E> error = new ErrorResponse<>(errorType, errorDetails);

        return new BaseResponse<>(meta, null, error);
    }

}
