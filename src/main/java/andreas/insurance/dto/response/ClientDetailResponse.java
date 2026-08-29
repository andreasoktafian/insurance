package andreas.insurance.dto.response;

import andreas.insurance.dto.map.ClientDetails;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ClientDetailResponse(

        String clientNumber,
        String clientName,
        LocalDate birthDate,
        String idNumber,
        String genderCode

) {

    public static ClientDetailResponse fromEntity(ClientDetails entity) {
        return new ClientDetailResponse(
                entity.cliNum(),
                entity.cliNm(),
                entity.birthDt(),
                entity.idNum(),
                entity.sexCode()
        );
    }

}
