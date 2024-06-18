package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.component.ReportCalculator;
import cs.vsu.ru.tpbakebudget.dto.request.orders.ReportRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.orders.IncomeResponseDTO;
import cs.vsu.ru.tpbakebudget.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportsControllerTest {

    @Mock
    private ReportCalculator calculator;

    @InjectMocks
    private ReportsController reportsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Users mockUser = new Users();
        mockUser.setId(1L);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCalculationByOrder() {
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        Map<String, Integer> report = new HashMap<>();
        report.put("item1", 10);

        when(calculator.calculateByOrders(anyLong(), any(), any(), any(), any())).thenReturn(report);

        ResponseEntity<?> responseEntity = reportsController.calculationByOrder(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(report, responseEntity.getBody());

        verify(calculator, times(1)).calculateByOrders(anyLong(), any(), any(), any(), any());
    }

    @Test
    void testCalculationByOrder_NoContent() {
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        Map<String, Integer> report = new HashMap<>();

        when(calculator.calculateByOrders(anyLong(), any(), any(), any(), any())).thenReturn(report);

        ResponseEntity<?> responseEntity = reportsController.calculationByOrder(requestDTO);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        verify(calculator, times(1)).calculateByOrders(anyLong(), any(), any(), any(), any());
    }

    @Test
    void testCalculationByIncome() {
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        IncomeResponseDTO incomeResponseDTO = new IncomeResponseDTO(100, 100);

        when(calculator.calculateByIncome(anyLong(), any(), any(), any(), any())).thenReturn(incomeResponseDTO);

        ResponseEntity<IncomeResponseDTO> responseEntity = reportsController.calculationByIncome(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(incomeResponseDTO, responseEntity.getBody());

        verify(calculator, times(1)).calculateByIncome(anyLong(), any(), any(), any(), any());
    }

    @Test
    void testCalculationByOrderGroup() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode("group1");
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        Map<String, Integer> report = new HashMap<>();
        report.put("item1", 10);

        when(calculator.calculateByOrdersForGroup(anyString(), any(), any(), any(), any())).thenReturn(report);

        ResponseEntity<Map<String, Integer>> responseEntity = reportsController.calculationByOrderGroup(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(report, responseEntity.getBody());

        verify(calculator, times(1)).calculateByOrdersForGroup(anyString(), any(), any(), any(), any());
    }

    @Test
    void testCalculationByIncomeGroup() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setGroupCode("group1");
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        IncomeResponseDTO incomeResponseDTO = new IncomeResponseDTO(100, 100);

        when(calculator.calculateByIncomeForGroup(anyString(), any(), any(), any(), any())).thenReturn(incomeResponseDTO);

        ResponseEntity<IncomeResponseDTO> responseEntity = reportsController.calculationByIncomeGroup(requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(incomeResponseDTO, responseEntity.getBody());

        verify(calculator, times(1)).calculateByIncomeForGroup(anyString(), any(), any(), any(), any());
    }
}
