package wis.my_spring_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wis.my_spring_project.dto.DashboardDTO;
import wis.my_spring_project.repository.InventoryRepository;
import wis.my_spring_project.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public DashboardDTO getDashboard() {
        return DashboardDTO.builder()
                .totalProducts(productRepository.count())
                .totalLocations((long) inventoryRepository.findDistinctLocations().size())
                .totalInventoryRecords(inventoryRepository.count())
                .totalQuantity(inventoryRepository.sumTotalQuantity())
                .build();
    }
}
