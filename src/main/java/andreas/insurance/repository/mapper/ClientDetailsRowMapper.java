package andreas.insurance.repository.mapper;

import andreas.insurance.dto.map.ClientDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ClientDetailsRowMapper implements RowMapper<ClientDetails> {

    @Override
    public ClientDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

        LocalDate birthDt = rs.getObject("BIRTH_DT", LocalDate.class);

        return new ClientDetails(
                rs.getString("CLI_NUM"),
                rs.getString("CLI_NM"),
                birthDt,
                rs.getString("ID_NUM"),
                rs.getString("SEX_CODE")
        );
    }

}
