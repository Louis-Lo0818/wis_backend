package wis.my_spring_project.controller;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TransferLogRepository transferLogRepository;

    @BeforeEach
    void setUp() {
        transferLogRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    // ─── GET /api/dashboard ───────────────────────────────────────────────────

    @Test
    void getDashboard_emptyDb_returnsZeroCounts() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalProducts", is(0)))
                .andExpect(jsonPath("$.totalLocations", is(0)))
                .andExpect(jsonPath("$.totalInventoryRecords", is(0)))
                .andExpect(jsonPath("$.totalQuantity", is(0)));
    }

    @Test
    void getDashboard_withProductsAndInventory_returnsCorrectCounts() throws Exception {
        Product p1 = productRepository.save(Product.builder()
                .code("PRD001").name("Laptop").weight(BigDecimal.valueOf(2.5)).build());
        Product p2 = productRepository.save(Product.builder()
                .code("PRD002").name("Mouse").weight(BigDecimal.valueOf(0.1)).build());

        inventoryRepository.save(Inventory.builder().product(p1).location("TKO").quantity(100).build());
        inventoryRepository.save(Inventory.builder().product(p1).location("CSW").quantity(50).build());
        inventoryRepository.save(Inventory.builder().product(p2).location("TKO").quantity(200).build());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts", is(2)))
                .andExpect(jsonPath("$.totalLocations", is(2)))          // TKO, CSW (distinct)
                .andExpect(jsonPath("$.totalInventoryRecords", is(3)))   // 3 inventory rows
                .andExpect(jsonPath("$.totalQuantity", is(350)));        // 100+50+200
    }

    @Test
    void getDashboard_onlyProducts_noInventory_returnsCorrectCounts() throws Exception {
        productRepository.save(Product.builder()
                .code("PRD001").name("Laptop").weight(BigDecimal.valueOf(2.5)).build());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts", is(1)))
                .andExpect(jsonPath("$.totalLocations", is(0)))
                .andExpect(jsonPath("$.totalInventoryRecords", is(0)))
                .andExpect(jsonPath("$.totalQuantity", is(0)));
    }
}
