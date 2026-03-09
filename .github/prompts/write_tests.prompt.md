---
description: "Write JUnit 5 tests for a service or controller"
---

# Write Backend Tests

Write JUnit 5 tests for Warehouse Inventory backend services and controllers.

## Test Patterns

### Unit Tests (Service Layer)
Use Mockito to mock repositories:

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock private Repository repo;
    @InjectMocks private Service service;

    @Test
    void methodName_scenario_expectedResult() {
        // given
        when(repo.findByCode("X")).thenReturn(Optional.of(entity));
        // when
        Result result = service.method(input);
        // then
        assertEquals(expected, result);
        verify(repo).save(any(Entity.class));
    }
}
```

### Integration Tests (Controller Layer)
Use @SpringBootTest + MockMvc:

```java
@SpringBootTest
@AutoConfigureMockMvc
class ControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void endpoint_scenario_expectedStatus() throws Exception {
        mockMvc.perform(post("/api/endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.field").value("expected"));
    }
}
```

## Test Cases for Transfer Service (Reference)
1. ✅ Successful transfer (deduct source, add destination, log created)
2. ❌ Product not found → ResourceNotFoundException
3. ❌ Source location not found → ResourceNotFoundException
4. ❌ Insufficient quantity → InsufficientQuantityException
5. ✅ Transfer to new location (upsert destination inventory)
6. ❌ Transfer to same location → should be prevented

## Test Naming Convention
`methodName_scenario_expectedBehavior`

Examples:
- `transfer_success_deductsSourceAndAddsDestination`
- `transfer_insufficientQty_throwsException`
- `transfer_productNotFound_throwsNotFoundException`

---

**Default behavior**: Write unit tests for the specified service.
**To override**: Say "integration tests" to write controller-level tests instead.
