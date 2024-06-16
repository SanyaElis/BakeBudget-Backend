package cs.vsu.ru.tpbakebudget.service;

import cs.vsu.ru.tpbakebudget.exception.NotFoundException;
import cs.vsu.ru.tpbakebudget.model.Products;
import cs.vsu.ru.tpbakebudget.model.Users;
import cs.vsu.ru.tpbakebudget.repository.ProductsRepository;
import cs.vsu.ru.tpbakebudget.service.impl.ProductsServiceImpl;
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

public class ProductsServiceImplTests {

    @Mock
    private ProductsRepository repository;

    @InjectMocks
    private ProductsServiceImpl service;

    private Products product1;
    private Products product2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Users user = new Users();
        user.setId(1L);

        product1 = new Products();
        product1.setId(1L);
        product1.setName("Cake1");
        product1.setUser(user);

        product2 = new Products();
        product2.setId(2L);
        product2.setName("Cake2");
        product2.setUser(user);
    }

    @Test
    public void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Products> products = service.findAll();

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testSaveNewProduct() {
        when(repository.existsByUserIdAndName(1L, "Cake1")).thenReturn(false);
        when(repository.save(product1)).thenReturn(product1);

        Products savedProduct = service.save(product1);

        assertNotNull(savedProduct);
        assertEquals(product1.getName(), savedProduct.getName());
        verify(repository, times(1)).existsByUserIdAndName(1L, "Cake1");
        verify(repository, times(1)).save(product1);
    }

    @Test
    public void testSaveExistingProduct() {
        when(repository.existsByUserIdAndName(1L, "Cake1")).thenReturn(true);

        Products savedProduct = service.save(product1);

        assertNull(savedProduct);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Cake1");
        verify(repository, times(0)).save(product1);
    }

    @Test
    public void testUpdateProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product1));
        when(repository.existsByUserIdAndName(1L, "Cake2")).thenReturn(false);
        when(repository.save(product1)).thenReturn(product1);

        Products updatedProduct = service.update(1L, product2);

        assertNotNull(updatedProduct);
        assertEquals(1L, updatedProduct.getId());
        assertEquals("Cake2", updatedProduct.getName());

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Cake2");
        verify(repository, times(1)).save(product1);
    }

    @Test
    public void testUpdateProductWithDuplicateName() {
        when(repository.findById(1L)).thenReturn(Optional.of(product1));
        when(repository.existsByUserIdAndName(1L, "Cake2")).thenReturn(true);

        Products updatedProduct = service.update(1L, product2);

        assertNull(updatedProduct);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Cake2");
        verify(repository, times(0)).save(product2);
    }

    @Test
    public void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product1));

        Products foundProduct = service.findById(1L);

        assertNotNull(foundProduct);
        assertEquals(1L, foundProduct.getId());
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
        List<Products> productsList = Arrays.asList(product1, product2);
        when(repository.saveAll(productsList)).thenReturn(productsList);

        List<Products> savedProducts = service.saveAll(productsList);

        assertNotNull(savedProducts);
        assertEquals(2, savedProducts.size());
        verify(repository, times(1)).saveAll(productsList);
    }

    @Test
    public void testFindAllByUserId() {
        when(repository.findAllByUserId(1L)).thenReturn(Arrays.asList(product1, product2));

        List<Products> products = service.findAllByUserId(1L);

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(repository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testExistsByUserIdAndName() {
        when(repository.existsByUserIdAndName(1L, "Cake1")).thenReturn(true);

        boolean exists = service.existsByUserIdAndName(1L, "Cake1");

        assertTrue(exists);
        verify(repository, times(1)).existsByUserIdAndName(1L, "Cake1");
    }

    @Test
    public void testSetMinioName() {
        String minioName = "minioPicture";
        service.setMinioName(product1, minioName);

        assertEquals(minioName, product1.getMinioPictureName());
        verify(repository, times(1)).save(product1);
    }
}
