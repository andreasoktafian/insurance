package andreas.insurance.controller;

import andreas.insurance.dto.context.AppRequestContext;
import andreas.insurance.dto.response.BaseResponse;
import andreas.insurance.dto.response.ClientDetailResponse;
import andreas.insurance.dto.response.DukcapilDetailResponse;
import andreas.insurance.exception.custom.ResourceNotFoundException;
import andreas.insurance.service.ClientService;
import andreas.insurance.service.DukcapilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private DukcapilService dukcapilService;

    @InjectMocks
    private PolicyController policyController;

    AppRequestContext context = new AppRequestContext("20260001", UUID.randomUUID().toString());

    @Test
    void getPolicyOwner_Success_ReturnsClientList() {

        String policyNumber = "123";

        ClientDetailResponse mockClient = new ClientDetailResponse(
                "CLI-001",
                "Budi Santoso",
                LocalDate.of(1985, 5, 12),
                "3171234567890001",
                "M"
        );

        when(clientService.getPolicyOwnerByPolicyNumber(policyNumber))
                .thenReturn(List.of(mockClient));

        ResponseEntity<BaseResponse<List<ClientDetailResponse>>> responseEntity =
                policyController.getPolicyOwner(policyNumber, context);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        List<ClientDetailResponse> data = responseEntity.getBody().data();
        assertEquals(1, data.size());
        assertEquals("CLI-001", data.getFirst().clientNumber());
        assertEquals("Budi Santoso", data.getFirst().clientName());

        verify(clientService, times(1)).getPolicyOwnerByPolicyNumber(policyNumber);

    }

    @Test
    void getPolicyOwner_NotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "999";

        when(clientService.getPolicyOwnerByPolicyNumber(policyNumber))
                .thenThrow(new ResourceNotFoundException("Policy with number 999 not found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> policyController.getPolicyOwner(policyNumber, context)
        );

        assertEquals("Policy with number 999 not found", exception.getMessage());
        verify(clientService, times(1)).getPolicyOwnerByPolicyNumber(policyNumber);
    }

    @Test
    void getPolicyOwner_OwnerNotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "123";

        when(clientService.getPolicyOwnerByPolicyNumber(policyNumber))
                .thenThrow(new ResourceNotFoundException("Client could not be found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> policyController.getPolicyOwner(policyNumber, context)
        );

        assertEquals("Client could not be found", exception.getMessage());
        verify(clientService, times(1)).getPolicyOwnerByPolicyNumber(policyNumber);
    }

    @Test
    void getInsuredDukcapil_Success_ReturnsDukcapilList() {

        String policyNumber = "123";

        LocalDateTime now = LocalDateTime.now();
        DukcapilDetailResponse mockDukcapil = new DukcapilDetailResponse(
                "CLI-002",
                "MATCH",
                "MATCH",
                "MATCH",
                "MATCH",
                "ALL DATA MATCHED",
                "00"
        );

        when(dukcapilService.getInsuredDukcapilByPolicy(policyNumber))
                .thenReturn(List.of(mockDukcapil));

        ResponseEntity<BaseResponse<List<DukcapilDetailResponse>>> responseEntity =
                policyController.getInsuredDukcapil(policyNumber, context);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        List<DukcapilDetailResponse> data = responseEntity.getBody().data();
        assertNotNull(data);
        assertEquals(1, data.size());
        assertEquals("CLI-002", data.getFirst().clientNumber());
        assertEquals("MATCH", data.getFirst().idMatchStatus());
        assertEquals("ALL DATA MATCHED", data.getFirst().summary());

        verify(dukcapilService, times(1)).getInsuredDukcapilByPolicy(policyNumber);
    }

    @Test
    void getInsuredDukcapil_NotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "999";

        when(dukcapilService.getInsuredDukcapilByPolicy(policyNumber))
                .thenThrow(new ResourceNotFoundException("Policy with number 999 not found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> policyController.getInsuredDukcapil(policyNumber, context)
        );

        assertEquals("Policy with number 999 not found", exception.getMessage());
        verify(dukcapilService, times(1)).getInsuredDukcapilByPolicy(policyNumber);
    }

    @Test
    void getInsuredDukcapil_DataNotFound_ThrowsResourceNotFoundException() {

        String policyNumber = "123";

        when(dukcapilService.getInsuredDukcapilByPolicy(policyNumber))
                .thenThrow(new ResourceNotFoundException("Client Dukcapil Data could not be found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> policyController.getInsuredDukcapil(policyNumber, context)
        );

        assertEquals("Client Dukcapil Data could not be found", exception.getMessage());
        verify(dukcapilService, times(1)).getInsuredDukcapilByPolicy(policyNumber);
    }

}
