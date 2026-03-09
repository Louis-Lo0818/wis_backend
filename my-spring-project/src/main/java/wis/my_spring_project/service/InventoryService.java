package wis.my_spring_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wis.my_spring_project.dto.InventoryLevelDTO;
import wis.my_spring_project.entity.Inventory;
import wis.my_spring_project.repository.InventoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryLevelDTO> getAllInventory() {
        return inventoryRepository.findAllWithProduct().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryLevelDTO> searchByProductCode(String code) {
        return inventoryRepository.findByProductCodeWithProduct(code).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getLocations() {
        return inventoryRepository.findDistinctLocations();
    }

    private InventoryLevelDTO toDTO(Inventory inventory) {
        return InventoryLevelDTO.builder()
                .productCode(inventory.getProduct().getCode())
                .productName(inventory.getProduct().getName())
                .location(inventory.getLocation())
                .quantity(inventory.getQuantity())
                .build();
    }
}
