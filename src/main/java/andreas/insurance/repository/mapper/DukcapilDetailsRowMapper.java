package andreas.insurance.repository.mapper;

import andreas.insurance.dto.map.DukcapilDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DukcapilDetailsRowMapper implements RowMapper<DukcapilDetails> {

    @Override
    public DukcapilDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DukcapilDetails(
                rs.getString("CLI_NUM"),
                rs.getString("ID_NUM_RESULT"),
                rs.getString("CLI_NM_RESULT"),
                rs.getString("BIRTH_DT_RESULT"),
                rs.getString("SEX_CODE_RESULT"),
                rs.getString("SUMMARY_RESULT"),
                rs.getString("SUMMARY_CODE"),
                rs.getString("RESULT_CODE"),
                rs.getObject("CREATE_DATE", LocalDateTime.class),
                rs.getObject("LASTUPDATE_DATE", LocalDateTime.class)
        );
    }

}
