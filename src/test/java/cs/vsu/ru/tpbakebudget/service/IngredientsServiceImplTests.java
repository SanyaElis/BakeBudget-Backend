package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.IngredientsRepository;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IngredientsServiceImplTests {

    @Mock
    private IngredientsRepository repository;

    @InjectMocks
    private IngredientsServiceImpl service;

    private Ingredients ingredient1;
    private Ingredients ingredient2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Users user = new Users();
        user.setId(1L);

        ingredient1 = new Ingredients();
        ingredient1.setId(1L);
        ingredient1.setName("Sugar");
        ingredient1.setUser(user);

        ingredient2 = new Ingredients();
        ingredient2.setId(2L);
        ingredient2.setName("Salt");
        ingredient2.setUser(user);
    }

    @Test
    public void testFindAllByUserId() {
        when(repository.findAllByUserId(1L)).thenReturn(Arrays.asList(ingredient1, ingredient2));

        List<Ingredients> ingredients = service.findAllByUserId(1L);

        assertNotNull(ingredients);
        assertEquals(2, ingredients.size());
        verify(repository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void testSaveNewIngredient() {
        when(repository.existsByUserIdAndName(1L, "Sugar")).thenReturn(false);
        when(repository.save(ingredient1)).thenReturn(ingredient1);

        Ingredients savedIngredient = service.save(ingredient1);

        assertNotNull(savedIngredient);
        assertEquals(ingredient1.getName(), savedIngredient.getName());
        verify(repository, times(1)).existsByUserIdAndName(1L, "Sugar");
        verify(repository, times(1)).save(ingredient1);
    }

    @Test
    public void testSaveExistingIngredient() {
        when(repository.existsByUserIdAndName(1L, "Sugar")).thenReturn(true);

        Ingredients savedIngredient = service.save(ingredient1);

        assertNull(savedIngredient);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Sugar");
        verify(repository, times(0)).save(ingredient1);
    }

    @Test
    public void testUpdateIngredient() {
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient1));
        when(repository.existsByUserIdAndName(1L, "Salt")).thenReturn(false);
        when(repository.save(ingredient2)).thenReturn(ingredient2);

        Ingredients updatedIngredient = service.update(1L, ingredient2);

        assertNotNull(updatedIngredient);
        assertEquals(1L, updatedIngredient.getId());
        assertEquals("Salt", updatedIngredient.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Salt");
        verify(repository, times(1)).save(ingredient2);
    }

    @Test
    public void testUpdateIngredientWithDuplicateName() {
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient1));
        when(repository.existsByUserIdAndName(1L, "Salt")).thenReturn(true);

        Ingredients updatedIngredient = service.update(1L, ingredient2);

        assertNull(updatedIngredient);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Salt");
        verify(repository, times(0)).save(ingredient2);
    }

    @Test
    public void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient1));

        Ingredients foundIngredient = service.findById(1L);

        assertNotNull(foundIngredient);
        assertEquals(1L, foundIngredient.getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testSaveAll() {
        List<Ingredients> ingredientsList = Arrays.asList(ingredient1, ingredient2);
        when(repository.saveAll(ingredientsList)).thenReturn(ingredientsList);

        List<Ingredients> savedIngredients = service.saveAll(ingredientsList);

        assertNotNull(savedIngredients);
        assertEquals(2, savedIngredients.size());
        verify(repository, times(1)).saveAll(ingredientsList);
    }

    @Test
    public void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient1));

        service.delete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(1L));
        verify(repository, times(1)).findById(1L);
        verify(repository, times(0)).deleteById(1L);
    }

    @Test
    public void testFindByIngredientsInProductPkProductId() {
        when(repository.findByIngredientsInProductPkProductId(1L)).thenReturn(Arrays.asList(ingredient1, ingredient2));

        List<Ingredients> ingredients = service.findByIngredientsInProductPkProductId(1L);

        assertNotNull(ingredients);
        assertEquals(2, ingredients.size());
        verify(repository, times(1)).findByIngredientsInProductPkProductId(1L);
    }

    @Test
    public void testExistsByUserIdAndName() {
        when(repository.existsByUserIdAndName(1L, "Sugar")).thenReturn(true);

        boolean exists = service.existsByUserIdAndName(1L, "Sugar");

        assertTrue(exists);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Sugar");
    }
}
