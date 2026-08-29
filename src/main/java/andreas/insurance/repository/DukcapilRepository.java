package andreas.insurance.repository;

import andreas.insurance.dto.map.DukcapilDetails;
import andreas.insurance.repository.mapper.DukcapilDetailsRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DukcapilRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<DukcapilDetails> findInsuredDukcapilByPolicyNumber(String policyNumber) {

        String sql = """
            SELECT dd.*
            FROM TCLIENT_DUKCAPIL_DETAILS dd
            JOIN TCLIENT_POLICY_LINKS cpl ON dd.CLI_NUM = cpl.CLI_NUM
            WHERE cpl.POL_NUM = ? 
              AND cpl.LINK_TYP = 'I'
            """;

        return jdbcTemplate.query(sql, new DukcapilDetailsRowMapper(), policyNumber);

    }

}
