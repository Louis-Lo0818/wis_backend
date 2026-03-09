package wis.my_spring_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wis.my_spring_project.dto.InventoryLevelDTO;
import wis.my_spring_project.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryLevelDTO>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryLevelDTO>> searchByProductCode(@RequestParam String code) {
        return ResponseEntity.ok(inventoryService.searchByProductCode(code));
    }

    @GetMapping("/locations")
    public ResponseEntity<List<String>> getLocations() {
        return ResponseEntity.ok(inventoryService.getLocations());
    }
}
