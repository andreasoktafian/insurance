package andreas.insurance.dto.context;

public record AppRequestContext(

        String actionBy,
        String correlationId

) {
}
