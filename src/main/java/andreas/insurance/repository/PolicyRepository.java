package andreas.insurance.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PolicyRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean existsByPolicyNumber(String policyNumber) {

        String sql = "SELECT COUNT(1) FROM TPOLICYS WHERE POL_NUM = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, policyNumber);

        return count != null && count > 0;

    }

}
