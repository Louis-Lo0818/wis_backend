package wis.my_spring_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class TransferService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final TransferLogRepository transferLogRepository;

    @Transactional
    public TransferResponseDTO transfer(TransferRequestDTO request) {
        Product product = productRepository.findByCode(request.getProductCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with code: " + request.getProductCode()));

        Inventory source = inventoryRepository.findByProductAndLocation(product, request.getFromLocation())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory found for product " + request.getProductCode()
                                + " at location " + request.getFromLocation()));

        if (source.getQuantity() < request.getQuantity()) {
            throw new InsufficientQuantityException(
                    "Insufficient quantity at " + request.getFromLocation()
                            + ". Available: " + source.getQuantity()
                            + ", Requested: " + request.getQuantity());
        }

        source.setQuantity(source.getQuantity() - request.getQuantity());
        inventoryRepository.save(source);

        Inventory destination = inventoryRepository.findByProductAndLocation(product, request.getToLocation())
                .orElse(Inventory.builder()
                        .product(product)
                        .location(request.getToLocation())
                        .quantity(0)
                        .build());
        destination.setQuantity(destination.getQuantity() + request.getQuantity());
        inventoryRepository.save(destination);

        TransferLog log = TransferLog.builder()
                .product(product)
                .fromLocation(request.getFromLocation())
                .toLocation(request.getToLocation())
                .quantity(request.getQuantity())
                .build();
        transferLogRepository.save(log);

        return TransferResponseDTO.builder()
                .status("SUCCESS")
                .message("Transferred " + request.getQuantity() + " units of "
                        + request.getProductCode() + " from " + request.getFromLocation()
                        + " to " + request.getToLocation())
                .build();
    }
}
