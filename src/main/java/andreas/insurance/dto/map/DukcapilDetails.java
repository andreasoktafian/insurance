package andreas.insurance.dto.map;

import java.time.LocalDateTime;

public record DukcapilDetails(

        String cliNum,
        String idNumResult,
        String cliNmResult,
        String birthDtResult,
        String sexCodeResult,
        String summaryResult,
        String summaryCode,
        String resultCode,
        LocalDateTime createDate,
        LocalDateTime lastupdateDate

) {
}
