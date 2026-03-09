package wis.my_spring_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import wis.my_spring_project.entity.Inventory;
import wis.my_spring_project.entity.Product;
import wis.my_spring_project.repository.InventoryRepository;
import wis.my_spring_project.repository.ProductRepository;
import wis.my_spring_project.repository.TransferLogRepository;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TransferLogRepository transferLogRepository;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        transferLogRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();

        savedProduct = productRepository.save(
                Product.builder()
                        .code("PRD001")
                        .name("Laptop")
                        .weight(BigDecimal.valueOf(2.5))
                        .build()
        );
    }

    // ─── POST /api/transfers (success) ───────────────────────────────────────

    @Test
    void transfer_success_returns200AndDeductsSource() throws Exception {
        inventoryRepository.save(Inventory.builder()
                .product(savedProduct).location("TKO").quantity(100).build());
        inventoryRepository.save(Inventory.builder()
                .product(savedProduct).location("CSW").quantity(20).build());

        Map<String, Object> request = Map.of(
                "productCode", "PRD001",
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", 30
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", containsString("PRD001")));
    }

    @Test
    void transfer_success_createsNewDestination() throws Exception {
        inventoryRepository.save(Inventory.builder()
                .product(savedProduct).location("TKO").quantity(100).build());

        Map<String, Object> request = Map.of(
                "productCode", "PRD001",
                "fromLocation", "TKO",
                "toLocation", "GDNO",
                "quantity", 25
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")));
    }

    // ─── POST /api/transfers (product not found) ─────────────────────────────

    @Test
    void transfer_productNotFound_returns404() throws Exception {
        Map<String, Object> request = Map.of(
                "productCode", "NOTEXIST",
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", 10
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    // ─── POST /api/transfers (source inventory not found) ────────────────────

    @Test
    void transfer_sourceInventoryNotFound_returns404() throws Exception {
        // No inventory seeded — source location doesn't exist
        Map<String, Object> request = Map.of(
                "productCode", "PRD001",
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", 10
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    // ─── POST /api/transfers (insufficient quantity) ─────────────────────────

    @Test
    void transfer_insufficientQty_returns400() throws Exception {
        inventoryRepository.save(Inventory.builder()
                .product(savedProduct).location("TKO").quantity(5).build());

        Map<String, Object> request = Map.of(
                "productCode", "PRD001",
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", 100
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("5")));
    }

    // ─── POST /api/transfers (validation errors) ─────────────────────────────

    @Test
    void transfer_missingProductCode_returns400() throws Exception {
        Map<String, Object> request = Map.of(
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", 10
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void transfer_negativeQuantity_returns400() throws Exception {
        Map<String, Object> request = Map.of(
                "productCode", "PRD001",
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", -5
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    void transfer_zeroQuantity_returns400() throws Exception {
        Map<String, Object> request = Map.of(
                "productCode", "PRD001",
                "fromLocation", "TKO",
                "toLocation", "CSW",
                "quantity", 0
        );

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
