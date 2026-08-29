package andreas.insurance.dto.map;

import java.time.LocalDate;

public record ClientDetails(

        String cliNum,
        String cliNm,
        LocalDate birthDt,
        String idNum,
        String sexCode

) {

}
