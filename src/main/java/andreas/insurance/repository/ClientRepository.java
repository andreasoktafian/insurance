package andreas.insurance.repository;

import andreas.insurance.dto.map.ClientDetails;
import andreas.insurance.repository.mapper.ClientDetailsRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepository {

    private final JdbcTemplate jdbcTemplate;


    public List<ClientDetails> findPolicyOwnerByPolicyNumber(String policyNumber) {
        String sql = """
        SELECT cd.CLI_NUM, cd.CLI_NM, cd.BIRTH_DT, cd.ID_NUM, cd.SEX_CODE
        FROM TCLIENT_DETAILS cd
        JOIN TCLIENT_POLICY_LINKS cpl ON cd.CLI_NUM = cpl.CLI_NUM
        WHERE cpl.POL_NUM = ? 
          AND cpl.LINK_TYP = 'O'
        """;

        return jdbcTemplate.query(sql, new ClientDetailsRowMapper(), policyNumber);
    }

}
