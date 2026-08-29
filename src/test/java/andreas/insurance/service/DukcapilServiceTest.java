package andreas.insurance.service;

import andreas.insurance.dto.map.DukcapilDetails;
import andreas.insurance.dto.response.DukcapilDetailResponse;
import andreas.insurance.exception.custom.ResourceNotFoundException;
import andreas.insurance.repository.DukcapilRepository;
import andreas.insurance.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DukcapilServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private DukcapilRepository dukcapilRepository;

    @InjectMocks
    private DukcapilService dukcapilService;

    @Test
    void getInsuredDukcapilByPolicy_Success_ReturnsDukcapilDetailResponseList() {

        String policyNumber = "123";

        LocalDateTime now = LocalDateTime.now();
        DukcapilDetails mockEntity = new DukcapilDetails(
                "CLI-002",
                "MATCH",
                "MATCH",
                "MATCH",
                "MATCH",
                "ALL DATA MATCHED",
                "M001",
                "00",
                now,
                now
        );

        when(policyRepository.existsByPolicyNumber(policyNumber)).thenReturn(true);
        when(dukcapilRepository.findInsuredDukcapilByPolicyNumber(policyNumber))
                .thenReturn(List.of(mockEntity));

        List<DukcapilDetailResponse> responses = dukcapilService.getInsuredDukcapilByPolicy(policyNumber);

        assertNotNull(responses);
        assertEquals(1, responses.size());

        DukcapilDetailResponse firstResponse = responses.get(0);
        assertEquals("CLI-002", firstResponse.clientNumber());
        assertEquals("MATCH", firstResponse.idMatchStatus());
        assertEquals("ALL DATA MATCHED", firstResponse.summary());

        verify(policyRepository, times(1)).existsByPolicyNumber(policyNumber);
        verify(dukcapilRepository, times(1)).findInsuredDukcapilByPolicyNumber(policyNumber);

    }

    @Test
    void getInsuredDukcapilByPolicy_PolicyNotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "999";

        when(policyRepository.existsByPolicyNumber(policyNumber)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dukcapilService.getInsuredDukcapilByPolicy(policyNumber)
        );

        assertEquals("Policy with number 999 not found", exception.getMessage());

        verify(policyRepository, times(1)).existsByPolicyNumber(policyNumber);
        verify(dukcapilRepository, never()).findInsuredDukcapilByPolicyNumber(policyNumber);

    }

    @Test
    void getInsuredDukcapilByPolicy_DataNotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "123";

        when(policyRepository.existsByPolicyNumber(policyNumber)).thenReturn(true);
        when(dukcapilRepository.findInsuredDukcapilByPolicyNumber(policyNumber))
                .thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dukcapilService.getInsuredDukcapilByPolicy(policyNumber)
        );

        assertEquals("Client Dukcapil Data could not be found", exception.getMessage());

        verify(policyRepository, times(1)).existsByPolicyNumber(policyNumber);
        verify(dukcapilRepository, times(1)).findInsuredDukcapilByPolicyNumber(policyNumber);

    }

}
