package andreas.insurance.controller;

import andreas.insurance.dto.context.AppRequestContext;
import andreas.insurance.dto.response.BaseResponse;
import andreas.insurance.dto.response.ClientDetailResponse;
import andreas.insurance.dto.response.DukcapilDetailResponse;
import andreas.insurance.service.ClientService;
import andreas.insurance.service.DukcapilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies")
public class PolicyController {

    private final ClientService clientService;
    private final DukcapilService dukcapilService;

    @GetMapping("/{policyNumber}/owner")
    public ResponseEntity<BaseResponse<List<ClientDetailResponse>>> getPolicyOwner(
            @PathVariable String policyNumber,
            AppRequestContext context) {

        List<ClientDetailResponse> clients = clientService.getPolicyOwnerByPolicyNumber(policyNumber);

        return ResponseEntity.ok(BaseResponse.success(clients, "Client retrieved successfully"));
    }

    @GetMapping("/{policyNumber}/insured/dukcapil")
    public ResponseEntity<BaseResponse<List<DukcapilDetailResponse>>> getInsuredDukcapil(
            @PathVariable String policyNumber,
            AppRequestContext context) {

        List<DukcapilDetailResponse> dukcapilDetailResponses = dukcapilService.getInsuredDukcapilByPolicy(policyNumber);

        return ResponseEntity.ok(BaseResponse.success(dukcapilDetailResponses, "Client Dukcapil retrieved successfully"));
    }

}
