package wis.my_spring_project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wis.my_spring_project.dto.ProductDTO;
import wis.my_spring_project.entity.Product;
import wis.my_spring_project.exception.ResourceNotFoundException;
import wis.my_spring_project.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Product buildProduct(Long id, String code, String name) {
        return Product.builder()
                .id(id)
                .code(code)
                .name(name)
                .weight(BigDecimal.valueOf(1.5))
                .build();
    }

    private ProductDTO buildProductDTO(String code, String name) {
        return ProductDTO.builder()
                .code(code)
                .name(name)
                .weight(BigDecimal.valueOf(1.5))
                .build();
    }

    // ─── getAllProducts ────────────────────────────────────────────────────────

    @Test
    void getAllProducts_returnsAllProducts() {
        Product p1 = buildProduct(1L, "PRD001", "Laptop");
        Product p2 = buildProduct(2L, "PRD002", "Mouse");
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("PRD001", result.get(0).getCode());
        assertEquals("PRD002", result.get(1).getCode());
    }

    @Test
    void getAllProducts_emptyList_returnsEmpty() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTO> result = productService.getAllProducts();

        assertTrue(result.isEmpty());
    }

    // ─── getProductByCode ─────────────────────────────────────────────────────

    @Test
    void getProductByCode_success_returnsProductDTO() {
        Product product = buildProduct(1L, "PRD001", "Laptop");
        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductByCode("PRD001");

        assertNotNull(result);
        assertEquals("PRD001", result.getCode());
        assertEquals("Laptop", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void getProductByCode_notFound_throwsResourceNotFoundException() {
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductByCode("INVALID")
        );

        assertTrue(ex.getMessage().contains("INVALID"));
    }

    // ─── importProducts (upsert) ──────────────────────────────────────────────

    @Test
    void importProducts_newProduct_createsAndSaves() {
        ProductDTO dto = buildProductDTO("PRD001", "Laptop");
        when(productRepository.findByCode("PRD001")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p = Product.builder()
                    .id(1L).code(p.getCode()).name(p.getName()).weight(p.getWeight())
                    .build();
            return p;
        });

        List<ProductDTO> result = productService.importProducts(List.of(dto));

        assertEquals(1, result.size());
        assertEquals("PRD001", result.get(0).getCode());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void importProducts_existingProduct_updatesFields() {
        ProductDTO dto = buildProductDTO("PRD001", "Updated Laptop");
        dto.setWeight(BigDecimal.valueOf(3.0));

        Product existingProduct = buildProduct(1L, "PRD001", "Old Laptop");
        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        List<ProductDTO> result = productService.importProducts(List.of(dto));

        assertEquals(1, result.size());
        assertEquals("Updated Laptop", result.get(0).getName());
        assertEquals(BigDecimal.valueOf(3.0), result.get(0).getWeight());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void importProducts_multipleProducts_upsertAll() {
        ProductDTO dto1 = buildProductDTO("PRD001", "Laptop");
        ProductDTO dto2 = buildProductDTO("PRD002", "Mouse");

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.empty());
        when(productRepository.findByCode("PRD002")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            return Product.builder()
                    .id(1L).code(p.getCode()).name(p.getName()).weight(p.getWeight())
                    .build();
        });

        List<ProductDTO> result = productService.importProducts(List.of(dto1, dto2));

        assertEquals(2, result.size());
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void importProducts_emptyList_returnsEmpty() {
        List<ProductDTO> result = productService.importProducts(List.of());

        assertTrue(result.isEmpty());
        verify(productRepository, never()).save(any());
    }

    // ─── DTO mapping ──────────────────────────────────────────────────────────

    @Test
    void getProductByCode_mappingIsComplete() {
        Product product = buildProduct(42L, "PRD042", "Test Product");
        when(productRepository.findByCode("PRD042")).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductByCode("PRD042");

        assertEquals(42L, result.getId());
        assertEquals("PRD042", result.getCode());
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(1.5), result.getWeight());
    }
}
