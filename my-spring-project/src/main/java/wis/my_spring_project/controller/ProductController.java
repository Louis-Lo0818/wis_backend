package wis.my_spring_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wis.my_spring_project.dto.ProductDTO;
import wis.my_spring_project.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProductDTO> getProductByCode(@PathVariable String code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @PostMapping("/import")
    public ResponseEntity<List<ProductDTO>> importProducts(@Valid @RequestBody List<ProductDTO> products) {
        return ResponseEntity.ok(productService.importProducts(products));
    }
}
