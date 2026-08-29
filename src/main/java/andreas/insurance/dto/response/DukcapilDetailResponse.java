package andreas.insurance.dto.response;

import andreas.insurance.dto.map.DukcapilDetails;

public record DukcapilDetailResponse(

        String clientNumber,
        String idMatchStatus,
        String nameMatchStatus,
        String birthDateMatchStatus,
        String genderMatchStatus,
        String summary,
        String status

) {

    public static DukcapilDetailResponse fromEntity(DukcapilDetails entity) {
        return new DukcapilDetailResponse(
                entity.cliNum(),
                entity.idNumResult(),
                entity.cliNmResult(),
                entity.birthDtResult(),
                entity.sexCodeResult(),
                entity.summaryResult(),
                entity.resultCode()
        );
    }

}
