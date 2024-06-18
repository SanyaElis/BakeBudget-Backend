package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.request.products.ProductsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.products.ProductsResponseDTO;
import cs.vsu.ru.tpbakebudget.mapper.ProductsMapper;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.model.Users;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductsControllerTest {

    @Mock
    private ProductsServiceImpl productsService;

    @Mock
    private ProductsMapper productsMapper;

    @InjectMocks
    private ProductsController productsController;

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
    void testCreateProduct() {
        ProductsRequestDTO requestDTO = new ProductsRequestDTO();
        Products product = new Products();
        ProductsResponseDTO responseDTO = new ProductsResponseDTO();

        when(productsMapper.toEntity(any(ProductsRequestDTO.class), any(Users.class))).thenReturn(product);
        when(productsService.save(any(Products.class))).thenReturn(product);
        when(productsMapper.toDto(any(Products.class))).thenReturn(responseDTO);

        ResponseEntity<?> responseEntity = productsController.createProduct(requestDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(productsService, times(1)).save(any(Products.class));
    }

    @Test
    void testGetProductById() {
        Long id = 1L;
        Products product = new Products();
        ProductsResponseDTO responseDTO = new ProductsResponseDTO();

        when(productsService.findById(id)).thenReturn(product);
        when(productsMapper.toDto(any(Products.class))).thenReturn(responseDTO);

        ResponseEntity<ProductsResponseDTO> responseEntity = productsController.getProductById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(productsService, times(1)).findById(id);
    }

    @Test
    void testUpdateProduct() {
        Long id = 1L;
        ProductsRequestDTO requestDTO = new ProductsRequestDTO();
        Products product = new Products();
        ProductsResponseDTO responseDTO = new ProductsResponseDTO();

        when(productsMapper.toEntity(any(ProductsRequestDTO.class), any(Users.class))).thenReturn(product);
        when(productsService.update(eq(id), any(Products.class))).thenReturn(product);
        when(productsMapper.toDto(any(Products.class))).thenReturn(responseDTO);

        ResponseEntity<?> responseEntity = productsController.updateProduct(id, requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(productsService, times(1)).update(eq(id), any(Products.class));
    }

    @Test
    void testFindAllProducts() {
        Products product = new Products();
        ProductsResponseDTO responseDTO = new ProductsResponseDTO();

        when(productsService.findAllByUserId(any(Long.class))).thenReturn(Collections.singletonList(product));
        when(productsMapper.toDto(any(Products.class))).thenReturn(responseDTO);

        ResponseEntity<List<ProductsResponseDTO>> responseEntity = productsController.findAllProducts();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(responseDTO, responseEntity.getBody().get(0));

        verify(productsService, times(1)).findAllByUserId(any(Long.class));
    }

    @Test
    void testDeleteProductById() {
        Long id = 1L;

        ResponseEntity<?> responseEntity = productsController.deleteProductById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Product deleted successfully", responseEntity.getBody());

        verify(productsService, times(1)).delete(id);
    }
}
