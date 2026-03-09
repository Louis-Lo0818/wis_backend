package wis.my_spring_project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wis.my_spring_project.dto.TransferRequestDTO;
import wis.my_spring_project.dto.TransferResponseDTO;
import wis.my_spring_project.entity.Inventory;
import wis.my_spring_project.entity.Product;
import wis.my_spring_project.entity.TransferLog;
import wis.my_spring_project.exception.InsufficientQuantityException;
import wis.my_spring_project.exception.ResourceNotFoundException;
import wis.my_spring_project.repository.InventoryRepository;
import wis.my_spring_project.repository.ProductRepository;
import wis.my_spring_project.repository.TransferLogRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private TransferLogRepository transferLogRepository;

    @InjectMocks
    private TransferService transferService;

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Product buildProduct(String code) {
        return Product.builder()
                .id(1L)
                .code(code)
                .name("Test Product")
                .weight(BigDecimal.valueOf(1.5))
                .build();
    }

    private Inventory buildInventory(Product product, String location, int quantity) {
        return Inventory.builder()
                .id(1L)
                .product(product)
                .location(location)
                .quantity(quantity)
                .build();
    }

    // ─── Success Cases ────────────────────────────────────────────────────────

    @Test
    void transfer_success_deductsSourceAndAddsDestination() {
        Product product = buildProduct("PRD001");
        Inventory source = buildInventory(product, "TKO", 100);
        Inventory dest = buildInventory(product, "CSW", 20);

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductAndLocation(product, "TKO")).thenReturn(Optional.of(source));
        when(inventoryRepository.findByProductAndLocation(product, "CSW")).thenReturn(Optional.of(dest));

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("PRD001")
                .fromLocation("TKO")
                .toLocation("CSW")
                .quantity(30)
                .build();

        TransferResponseDTO response = transferService.transfer(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(70, source.getQuantity());
        assertEquals(50, dest.getQuantity());
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
        verify(transferLogRepository).save(any(TransferLog.class));
    }

    @Test
    void transfer_success_createsNewDestinationIfNotExists() {
        Product product = buildProduct("PRD001");
        Inventory source = buildInventory(product, "TKO", 100);

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductAndLocation(product, "TKO")).thenReturn(Optional.of(source));
        when(inventoryRepository.findByProductAndLocation(product, "NEW_LOCATION")).thenReturn(Optional.empty());

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("PRD001")
                .fromLocation("TKO")
                .toLocation("NEW_LOCATION")
                .quantity(40)
                .build();

        assertDoesNotThrow(() -> transferService.transfer(request));

        assertEquals(60, source.getQuantity());
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
        verify(transferLogRepository).save(any(TransferLog.class));
    }

    @Test
    void transfer_success_exactQuantity_reducesSourceToZero() {
        Product product = buildProduct("PRD001");
        Inventory source = buildInventory(product, "TKO", 50);

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductAndLocation(product, "TKO")).thenReturn(Optional.of(source));
        when(inventoryRepository.findByProductAndLocation(product, "CSW")).thenReturn(Optional.empty());

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("PRD001")
                .fromLocation("TKO")
                .toLocation("CSW")
                .quantity(50)
                .build();

        TransferResponseDTO response = transferService.transfer(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, source.getQuantity());
    }

    // ─── Failure Cases ────────────────────────────────────────────────────────

    @Test
    void transfer_productNotFound_throwsResourceNotFoundException() {
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("INVALID")
                .fromLocation("TKO")
                .toLocation("CSW")
                .quantity(10)
                .build();

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> transferService.transfer(request)
        );

        assertTrue(ex.getMessage().contains("INVALID"));
        verify(inventoryRepository, never()).findByProductAndLocation(any(), any());
        verify(transferLogRepository, never()).save(any());
    }

    @Test
    void transfer_sourceInventoryNotFound_throwsResourceNotFoundException() {
        Product product = buildProduct("PRD001");

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductAndLocation(product, "TKO")).thenReturn(Optional.empty());

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("PRD001")
                .fromLocation("TKO")
                .toLocation("CSW")
                .quantity(10)
                .build();

        assertThrows(
                ResourceNotFoundException.class,
                () -> transferService.transfer(request)
        );

        verify(transferLogRepository, never()).save(any());
    }

    @Test
    void transfer_insufficientQuantity_throwsInsufficientQuantityException() {
        Product product = buildProduct("PRD001");
        Inventory source = buildInventory(product, "TKO", 10);

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductAndLocation(product, "TKO")).thenReturn(Optional.of(source));

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("PRD001")
                .fromLocation("TKO")
                .toLocation("CSW")
                .quantity(50)
                .build();

        InsufficientQuantityException ex = assertThrows(
                InsufficientQuantityException.class,
                () -> transferService.transfer(request)
        );

        assertTrue(ex.getMessage().contains("10"));  // available quantity
        assertTrue(ex.getMessage().contains("50"));  // requested quantity
        assertEquals(10, source.getQuantity()); // source unchanged
        verify(inventoryRepository, never()).save(any());
        verify(transferLogRepository, never()).save(any());
    }

    @Test
    void transfer_quantityExceedsByOne_throwsInsufficientQuantityException() {
        Product product = buildProduct("PRD001");
        Inventory source = buildInventory(product, "TKO", 99);

        when(productRepository.findByCode("PRD001")).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductAndLocation(product, "TKO")).thenReturn(Optional.of(source));

        TransferRequestDTO request = TransferRequestDTO.builder()
                .productCode("PRD001")
                .fromLocation("TKO")
                .toLocation("CSW")
                .quantity(100)
                .build();

        assertThrows(InsufficientQuantityException.class, () -> transferService.transfer(request));
    }
}
