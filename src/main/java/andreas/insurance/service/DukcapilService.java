package andreas.insurance.service;

import andreas.insurance.annotation.LogBusinessEvent;
import andreas.insurance.dto.map.DukcapilDetails;
import andreas.insurance.dto.response.DukcapilDetailResponse;
import andreas.insurance.exception.custom.ResourceNotFoundException;
import andreas.insurance.repository.DukcapilRepository;
import andreas.insurance.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DukcapilService {

    private final PolicyRepository policyRepository;
    private final DukcapilRepository dukcapilRepository;

    @LogBusinessEvent("GET_INSURED_DUKCAPIL_BY_POLICY")
    public List<DukcapilDetailResponse> getInsuredDukcapilByPolicy(String policyNumber) {

        if (!policyRepository.existsByPolicyNumber(policyNumber)) {
            throw new ResourceNotFoundException("Policy with number " + policyNumber + " not found");
        }

        List<DukcapilDetails> dukcapilList = dukcapilRepository.findInsuredDukcapilByPolicyNumber(policyNumber);

        if (dukcapilList.isEmpty()) {
            throw new ResourceNotFoundException("Client Dukcapil Data could not be found");
        }

        return dukcapilList.stream()
                .map(DukcapilDetailResponse::fromEntity)
                .toList();
    }

}
