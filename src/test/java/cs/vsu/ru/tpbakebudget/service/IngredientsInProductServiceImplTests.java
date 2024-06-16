package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.model.Ingredients;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProduct;
import cs.vsu.ru.tpbakebudget.model.IngredientsInProductKey;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.repository.IngredientsInProductRepository;
import cs.vsu.ru.tpbakebudget.service.impl.IngredientsInProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IngredientsInProductServiceImplTests {

    @Mock
    private IngredientsInProductRepository repository;

    @InjectMocks
    private IngredientsInProductServiceImpl service;

    private IngredientsInProductKey key;
    private IngredientsInProduct ingredientsInProduct;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Ingredients ingredient = new Ingredients();
        ingredient.setId(1L);

        Products product = new Products();
        product.setId(1L);

        key = new IngredientsInProductKey(ingredient, product);

        ingredientsInProduct = new IngredientsInProduct();
        ingredientsInProduct.setPk(key);
    }

    @Test
    public void testSaveNewIngredientsInProduct() {
        when(repository.existsByPk(key)).thenReturn(false);
        when(repository.save(ingredientsInProduct)).thenReturn(ingredientsInProduct);

        IngredientsInProduct savedIngredientsInProduct = service.save(ingredientsInProduct);

        assertNotNull(savedIngredientsInProduct);
        assertEquals(key, savedIngredientsInProduct.getPk());
        verify(repository, times(1)).existsByPk(key);
        verify(repository, times(1)).save(ingredientsInProduct);
    }

    @Test
    public void testSaveExistingIngredientsInProduct() {
        when(repository.existsByPk(key)).thenReturn(true);

        IngredientsInProduct savedIngredientsInProduct = service.save(ingredientsInProduct);

        assertNull(savedIngredientsInProduct);
        verify(repository, times(1)).existsByPk(key);
        verify(repository, times(0)).save(ingredientsInProduct);
    }

    @Test
    public void testFindById() {
        when(repository.findById(key)).thenReturn(Optional.of(ingredientsInProduct));

        IngredientsInProduct foundIngredientsInProduct = service.findById(key);

        assertNotNull(foundIngredientsInProduct);
        assertEquals(key, foundIngredientsInProduct.getPk());
        verify(repository, times(1)).findById(key);
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(key)).thenReturn(Optional.empty());

        IngredientsInProduct foundIngredientsInProduct = service.findById(key);

        assertNull(foundIngredientsInProduct);
        verify(repository, times(1)).findById(key);
    }

    @Test
    public void testFindAll() {
        List<IngredientsInProduct> ingredientsInProducts = Collections.singletonList(ingredientsInProduct);
        when(repository.findAll()).thenReturn(ingredientsInProducts);

        List<IngredientsInProduct> foundIngredientsInProducts = service.findAll();

        assertNotNull(foundIngredientsInProducts);
        assertEquals(1, foundIngredientsInProducts.size());
        assertEquals(key, foundIngredientsInProducts.get(0).getPk());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testUpdateIngredientsInProduct() {
        when(repository.findById(key)).thenReturn(Optional.of(ingredientsInProduct));
        when(repository.save(ingredientsInProduct)).thenReturn(ingredientsInProduct);

        IngredientsInProduct updatedIngredientsInProduct = service.update(ingredientsInProduct);

        assertNotNull(updatedIngredientsInProduct);
        assertEquals(key, updatedIngredientsInProduct.getPk());
        verify(repository, times(1)).findById(key);
        verify(repository, times(1)).save(ingredientsInProduct);
    }

    @Test
    public void testUpdateNonExistingIngredientsInProduct() {
        when(repository.findById(key)).thenReturn(Optional.empty());

        IngredientsInProduct updatedIngredientsInProduct = service.update(ingredientsInProduct);

        assertNull(updatedIngredientsInProduct);
        verify(repository, times(1)).findById(key);
        verify(repository, times(0)).save(ingredientsInProduct);
    }

    @Test
    public void testDeleteIngredientsInProduct() {
        service.delete(key);

        verify(repository, times(1)).deleteById(key);
    }

    @Test
    public void testFindByProductId() {
        List<IngredientsInProduct> ingredientsInProducts = Collections.singletonList(ingredientsInProduct);
        when(repository.findByPk_ProductId(1L)).thenReturn(ingredientsInProducts);

        List<IngredientsInProduct> foundIngredientsInProducts = service.findByPk_ProductId(1L);

        assertNotNull(foundIngredientsInProducts);
        assertEquals(1, foundIngredientsInProducts.size());
        assertEquals(key, foundIngredientsInProducts.get(0).getPk());
        verify(repository, times(1)).findByPk_ProductId(1L);
    }

    @Test
    public void testExistsByPk() {
        when(repository.existsByPk(key)).thenReturn(true);

        boolean exists = service.existsByPk(key);

        assertTrue(exists);
        verify(repository, times(1)).existsByPk(key);
    }
}