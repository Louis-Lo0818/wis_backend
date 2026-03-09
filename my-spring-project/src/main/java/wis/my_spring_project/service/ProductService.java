package wis.my_spring_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wis.my_spring_project.dto.ProductDTO;
import wis.my_spring_project.entity.Product;
import wis.my_spring_project.exception.ResourceNotFoundException;
import wis.my_spring_project.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with code: " + code));
        return toDTO(product);
    }

    @Transactional
    public List<ProductDTO> importProducts(List<ProductDTO> products) {
        return products.stream().map(dto -> {
            Product product = productRepository.findByCode(dto.getCode())
                    .orElse(new Product());
            product.setCode(dto.getCode());
            product.setName(dto.getName());
            product.setWeight(dto.getWeight());
            return toDTO(productRepository.save(product));
        }).collect(Collectors.toList());
    }

    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .weight(product.getWeight())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
