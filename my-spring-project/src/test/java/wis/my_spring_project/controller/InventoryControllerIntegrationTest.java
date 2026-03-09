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
class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TransferLogRepository transferLogRepository;

    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        transferLogRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();

        productA = productRepository.save(Product.builder()
                .code("PRD001").name("Laptop").weight(BigDecimal.valueOf(2.5)).build());
        productB = productRepository.save(Product.builder()
                .code("PRD002").name("Mouse").weight(BigDecimal.valueOf(0.1)).build());
    }

    // ─── GET /api/inventory ───────────────────────────────────────────────────

    @Test
    void getAllInventory_emptyDb_returns200WithEmptyArray() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllInventory_withRecords_returnsAll() throws Exception {
        inventoryRepository.save(Inventory.builder().product(productA).location("TKO").quantity(100).build());
        inventoryRepository.save(Inventory.builder().product(productA).location("CSW").quantity(50).build());
        inventoryRepository.save(Inventory.builder().product(productB).location("TKO").quantity(200).build());

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].productCode", hasItems("PRD001", "PRD002")));
    }

    @Test
    void getAllInventory_responseContainsProductNameAndQuantity() throws Exception {
        inventoryRepository.save(Inventory.builder().product(productA).location("TKO").quantity(150).build());

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productCode", is("PRD001")))
                .andExpect(jsonPath("$[0].productName", is("Laptop")))
                .andExpect(jsonPath("$[0].location", is("TKO")))
                .andExpect(jsonPath("$[0].quantity", is(150)));
    }

    // ─── GET /api/inventory/search ────────────────────────────────────────────

    @Test
    void searchByProductCode_existingCode_returnsMatchingRows() throws Exception {
        inventoryRepository.save(Inventory.builder().product(productA).location("TKO").quantity(100).build());
        inventoryRepository.save(Inventory.builder().product(productA).location("CSW").quantity(50).build());
        inventoryRepository.save(Inventory.builder().product(productB).location("TKO").quantity(200).build());

        mockMvc.perform(get("/api/inventory/search").param("code", "PRD001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].productCode", everyItem(is("PRD001"))));
    }

    @Test
    void searchByProductCode_noMatch_returnsEmpty() throws Exception {
        mockMvc.perform(get("/api/inventory/search").param("code", "NONEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── GET /api/inventory/locations ─────────────────────────────────────────

    @Test
    void getLocations_noInventory_returnsEmpty() throws Exception {
        mockMvc.perform(get("/api/inventory/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getLocations_withInventory_returnsDistinctLocations() throws Exception {
        inventoryRepository.save(Inventory.builder().product(productA).location("TKO").quantity(100).build());
        inventoryRepository.save(Inventory.builder().product(productA).location("CSW").quantity(50).build());
        inventoryRepository.save(Inventory.builder().product(productB).location("TKO").quantity(200).build());

        mockMvc.perform(get("/api/inventory/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", containsInAnyOrder("TKO", "CSW")));
    }
}
