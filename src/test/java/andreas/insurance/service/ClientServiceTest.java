package andreas.insurance.service;

import andreas.insurance.dto.map.ClientDetails;
import andreas.insurance.dto.response.ClientDetailResponse;
import andreas.insurance.exception.custom.ResourceNotFoundException;
import andreas.insurance.repository.ClientRepository;
import andreas.insurance.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void getPolicyOwnerByPolicyNumber_Success_ReturnsClientDetailResponseList() {

        String policyNumber = "123";

        ClientDetails mockEntity = new ClientDetails(
                "CLI-001",
                "Budi Santoso",
                LocalDate.of(1985, 5, 12),
                "3171234567890001",
                "M"
        );

        when(policyRepository.existsByPolicyNumber(policyNumber)).thenReturn(true);
        when(clientRepository.findPolicyOwnerByPolicyNumber(policyNumber))
                .thenReturn(List.of(mockEntity));

        List<ClientDetailResponse> responses = clientService.getPolicyOwnerByPolicyNumber(policyNumber);

        assertNotNull(responses);
        assertEquals(1, responses.size());

        ClientDetailResponse firstResponse = responses.get(0);
        assertEquals("CLI-001", firstResponse.clientNumber());
        assertEquals("Budi Santoso", firstResponse.clientName());

        verify(policyRepository, times(1)).existsByPolicyNumber(policyNumber);
        verify(clientRepository, times(1)).findPolicyOwnerByPolicyNumber(policyNumber);

    }

    @Test
    void getPolicyOwnerByPolicyNumber_PolicyNotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "999";

        when(policyRepository.existsByPolicyNumber(policyNumber)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.getPolicyOwnerByPolicyNumber(policyNumber)
        );

        assertEquals("Policy with number 999 not found", exception.getMessage());

        verify(policyRepository, times(1)).existsByPolicyNumber(policyNumber);
        verify(clientRepository, never()).findPolicyOwnerByPolicyNumber(policyNumber);

    }

    @Test
    void getPolicyOwnerByPolicyNumber_OwnerNotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "123";

        when(policyRepository.existsByPolicyNumber(policyNumber)).thenReturn(true);
        when(clientRepository.findPolicyOwnerByPolicyNumber(policyNumber))
                .thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.getPolicyOwnerByPolicyNumber(policyNumber)
        );

        assertEquals("Client could not be found", exception.getMessage());

        verify(policyRepository, times(1)).existsByPolicyNumber(policyNumber);
        verify(clientRepository, times(1)).findPolicyOwnerByPolicyNumber(policyNumber);

    }

}
