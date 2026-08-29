package andreas.insurance.service;

import andreas.insurance.annotation.LogBusinessEvent;
import andreas.insurance.dto.map.ClientDetails;
import andreas.insurance.dto.response.ClientDetailResponse;
import andreas.insurance.exception.custom.ResourceNotFoundException;
import andreas.insurance.repository.ClientRepository;
import andreas.insurance.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final PolicyRepository policyRepository;
    private final ClientRepository clientRepository;

    @LogBusinessEvent("GET_POLICY_OWNER_BY_POLICY_NUMBER")
    public List<ClientDetailResponse> getPolicyOwnerByPolicyNumber(String policyNumber) {

        if (!policyRepository.existsByPolicyNumber(policyNumber)) {
            throw new ResourceNotFoundException("Policy with number " + policyNumber + " not found");
        }

        List<ClientDetails> ownerList = clientRepository.findPolicyOwnerByPolicyNumber(policyNumber);

        if (ownerList.isEmpty()) {
            throw new ResourceNotFoundException("Client could not be found");
        }

        return ownerList.stream()
                .map(ClientDetailResponse::fromEntity)
                .toList();
    }

}
