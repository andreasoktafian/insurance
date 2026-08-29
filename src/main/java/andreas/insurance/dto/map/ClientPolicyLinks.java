package andreas.insurance.dto.map;

public record ClientPolicyLinks(

        String polNum,
        String cliNum,
        String linkTyp,
        String relToInsrd,
        String addrTyp

) {
}
