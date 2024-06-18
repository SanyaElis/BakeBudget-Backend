package cs.vsu.ru.tpbakebudget.controller;

import cs.vsu.ru.tpbakebudget.dto.request.ingredients.IngredientsRequestDTO;
import cs.vsu.ru.tpbakebudget.dto.response.ingredients.IngredientsResponseDTO;
import cs.vsu.ru.tpbakebudget.mapper.IngredientsMapper;
import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsServiceImpl;
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

class IngredientsControllerTest {

    @Mock
    private IngredientsServiceImpl ingredientsService;

    @Mock
    private IngredientsMapper mapper;

    @InjectMocks
    private IngredientsController ingredientsController;

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
    void testCreateIngredient() {
        IngredientsRequestDTO requestDTO = new IngredientsRequestDTO();
        Ingredients ingredient = new Ingredients();
        IngredientsResponseDTO responseDTO = new IngredientsResponseDTO();

        when(mapper.toEntity(any(IngredientsRequestDTO.class), any(Users.class))).thenReturn(ingredient);
        when(ingredientsService.save(any(Ingredients.class))).thenReturn(ingredient);
        when(mapper.toDto(any(Ingredients.class))).thenReturn(responseDTO);

        ResponseEntity<?> responseEntity = ingredientsController.createIngredient(requestDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(ingredientsService, times(1)).save(any(Ingredients.class));
    }

    @Test
    void testGetIngredientById() {
        Long id = 1L;
        Ingredients ingredient = new Ingredients();
        IngredientsResponseDTO responseDTO = new IngredientsResponseDTO();

        when(ingredientsService.findById(id)).thenReturn(ingredient);
        when(mapper.toDto(any(Ingredients.class))).thenReturn(responseDTO);

        ResponseEntity<IngredientsResponseDTO> responseEntity = ingredientsController.getIngredientById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(ingredientsService, times(1)).findById(id);
    }

    @Test
    void testUpdateIngredient() {
        Long id = 1L;
        IngredientsRequestDTO requestDTO = new IngredientsRequestDTO();
        Ingredients ingredient = new Ingredients();
        IngredientsResponseDTO responseDTO = new IngredientsResponseDTO();

        when(mapper.toEntity(any(IngredientsRequestDTO.class), any(Users.class))).thenReturn(ingredient);
        when(ingredientsService.update(eq(id), any(Ingredients.class))).thenReturn(ingredient);
        when(mapper.toDto(any(Ingredients.class))).thenReturn(responseDTO);

        ResponseEntity<?> responseEntity = ingredientsController.updateIngredient(id, requestDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(responseDTO, responseEntity.getBody());

        verify(ingredientsService, times(1)).update(eq(id), any(Ingredients.class));
    }

    @Test
    void testFindAllIngredients() {
        Ingredients ingredient = new Ingredients();
        IngredientsResponseDTO responseDTO = new IngredientsResponseDTO();

        when(ingredientsService.findAllByUserId(any(Long.class))).thenReturn(Collections.singletonList(ingredient));
        when(mapper.toDto(any(Ingredients.class))).thenReturn(responseDTO);

        ResponseEntity<List<IngredientsResponseDTO>> responseEntity = ingredientsController.findAllIngredients();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(responseDTO, responseEntity.getBody().get(0));

        verify(ingredientsService, times(1)).findAllByUserId(any(Long.class));
    }

    @Test
    void testDeleteIngredientById() {
        Long id = 1L;

        ResponseEntity<?> responseEntity = ingredientsController.deleteIngredientById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Ingredient deleted successfully", responseEntity.getBody());

        verify(ingredientsService, times(1)).delete(id);
    }
}
