package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.request.outgoings.OutgoingsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.outgoings.OutgoingsResponseDTO;
import cs.vsu.ru.tpbakebudget.mapper.OutgoingsMapper;
import cs.vsu.ru.tpbakebudget.model.Outgoings;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.OutgoingsServiceImpl;
import cs.vsu.ru.tpbakebudget.service.impl.ProductsServiceImpl;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutgoingsControllerTest {

    @Mock
    private OutgoingsServiceImpl outgoingsService;

    @Mock
    private ProductsServiceImpl productsService;

    @Mock
    private OutgoingsMapper mapper;

    @InjectMocks
    private OutgoingsController outgoingsController;

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
    void testCreateOutgoing() {
        Long productId = 1L;
        OutgoingsRequestDTO requestDTO = new OutgoingsRequestDTO();
        Products product = new Products();
        Outgoings outgoing = new Outgoings();
        OutgoingsResponseDTO responseDTO = new OutgoingsResponseDTO();

        when(productsService.findById(productId)).thenReturn(product);
        when(mapper.toEntity(any(OutgoingsRequestDTO.class), any(Products.class))).thenReturn(outgoing);
        when(outgoingsService.save(any(Outgoings.class))).thenReturn(outgoing);
        when(mapper.toDto(any(Outgoings.class))).thenReturn(responseDTO);

        ResponseEntity<?> responseEntity = outgoingsController.createOutgoing(productId, requestDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(productsService, times(1)).findById(productId);
        verify(outgoingsService, times(1)).save(any(Outgoings.class));
    }

    @Test
    void testGetOutgoingById() {
        Long id = 1L;
        Outgoings outgoing = new Outgoings();
        OutgoingsResponseDTO responseDTO = new OutgoingsResponseDTO();

        when(outgoingsService.findById(id)).thenReturn(outgoing);
        when(mapper.toDto(any(Outgoings.class))).thenReturn(responseDTO);

        ResponseEntity<OutgoingsResponseDTO> responseEntity = outgoingsController.getOutgoingById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(outgoingsService, times(1)).findById(id);
    }

    @Test
    void testUpdateOutgoing() {
        Long id = 1L;
        OutgoingsRequestDTO requestDTO = new OutgoingsRequestDTO();
        Outgoings oldOutgoing = new Outgoings();
        Products product = new Products();
        oldOutgoing.setProduct(product);
        Outgoings updatedOutgoing = new Outgoings();
        OutgoingsResponseDTO responseDTO = new OutgoingsResponseDTO();

        when(outgoingsService.findById(id)).thenReturn(oldOutgoing);
        when(mapper.toEntity(any(OutgoingsRequestDTO.class), any(Products.class))).thenReturn(updatedOutgoing);
        when(outgoingsService.update(eq(id), any(Outgoings.class))).thenReturn(updatedOutgoing);
        when(mapper.toDto(any(Outgoings.class))).thenReturn(responseDTO);

        ResponseEntity<?> responseEntity = outgoingsController.updateOutgoing(id, requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(outgoingsService, times(1)).findById(id);
        verify(outgoingsService, times(1)).update(eq(id), any(Outgoings.class));
    }

    @Test
    void testFindAllOutgoings() {
        Long productId = 1L;
        Outgoings outgoing = new Outgoings();
        OutgoingsResponseDTO responseDTO = new OutgoingsResponseDTO();

        when(outgoingsService.findAllByProductId(productId)).thenReturn(Collections.singletonList(outgoing));
        when(mapper.toDto(any(Outgoings.class))).thenReturn(responseDTO);

        ResponseEntity<List<OutgoingsResponseDTO>> responseEntity = outgoingsController.findAllOutgoings(productId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(responseDTO, responseEntity.getBody().get(0));

        verify(outgoingsService, times(1)).findAllByProductId(productId);
    }

    @Test
    void testDeleteOutgoingById() {
        Long id = 1L;

        when(outgoingsService.findById(id)).thenReturn(new Outgoings());

        ResponseEntity<?> responseEntity = outgoingsController.deleteOutgoingById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Outgoing deleted successfully", responseEntity.getBody());

        verify(outgoingsService, times(1)).findById(id);
        verify(outgoingsService, times(1)).delete(id);
    }
}
