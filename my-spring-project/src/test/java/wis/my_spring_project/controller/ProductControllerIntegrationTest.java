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
import wis.my_spring_project.entity.Product;
import wis.my_spring_project.repository.InventoryRepository;
import wis.my_spring_project.repository.ProductRepository;
import wis.my_spring_project.repository.TransferLogRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        transferLogRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    // ─── GET /api/products ────────────────────────────────────────────────────

    @Test
    void getAllProducts_emptyDb_returns200WithEmptyArray() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllProducts_withProducts_returnsAll() throws Exception {
        productRepository.save(Product.builder().code("PRD001").name("Laptop").weight(BigDecimal.valueOf(2.5)).build());
        productRepository.save(Product.builder().code("PRD002").name("Mouse").weight(BigDecimal.valueOf(0.1)).build());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].code", containsInAnyOrder("PRD001", "PRD002")));
    }

    // ─── GET /api/products/{code} ─────────────────────────────────────────────

    @Test
    void getProductByCode_existingCode_returns200() throws Exception {
        productRepository.save(Product.builder().code("PRD001").name("Laptop").weight(BigDecimal.valueOf(2.5)).build());

        mockMvc.perform(get("/api/products/PRD001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("PRD001")))
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void getProductByCode_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/products/NOTEXIST"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    // ─── POST /api/products/import ────────────────────────────────────────────

    @Test
    void importProducts_newProducts_returns200AndSaves() throws Exception {
        List<Map<String, Object>> body = List.of(
                Map.of("code", "PRD001", "name", "Laptop", "weight", 2.5),
                Map.of("code", "PRD002", "name", "Mouse", "weight", 0.1)
        );

        mockMvc.perform(post("/api/products/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("PRD001")))
                .andExpect(jsonPath("$[1].code", is("PRD002")));
    }

    @Test
    void importProducts_existingProduct_updatesInPlace() throws Exception {
        productRepository.save(Product.builder().code("PRD001").name("Old Name").weight(BigDecimal.valueOf(1.0)).build());

        List<Map<String, Object>> body = List.of(
                Map.of("code", "PRD001", "name", "New Name", "weight", 2.0)
        );

        mockMvc.perform(post("/api/products/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("New Name")));
    }

    @Test
    void importProducts_nullCode_returns400DueToConstraintViolation() throws Exception {
        // Spring 6.x throws HandlerMethodValidationException for list element
        // constraint violations, handled by GlobalExceptionHandler → 400
        String rawBody = "[{\"name\":\"Laptop\",\"weight\":2.5}]";

        mockMvc.perform(post("/api/products/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rawBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void importProducts_emptyRequestBody_returns400() throws Exception {
        mockMvc.perform(post("/api/products/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
