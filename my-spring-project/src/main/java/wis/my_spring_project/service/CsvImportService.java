package wis.my_spring_project.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wis.my_spring_project.dto.ImportResultDTO;
import wis.my_spring_project.entity.Inventory;
import wis.my_spring_project.entity.Product;
import wis.my_spring_project.repository.InventoryRepository;
import wis.my_spring_project.repository.ProductRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ImportResultDTO importProductsCsv(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalProcessed = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] header = reader.readNext();
            if (header == null) {
                return ImportResultDTO.builder()
                        .success(false).importedCount(0).skippedCount(1).errors(List.of("Empty CSV file"))
                        .build();
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                totalProcessed++;
                try {
                    if (line.length < 3) {
                        errors.add("Row " + totalProcessed + ": insufficient columns");
                        continue;
                    }
                    String code = line[0].trim();
                    String name = line[1].trim();
                    BigDecimal weight = line.length > 2 && !line[2].trim().isEmpty()
                            ? new BigDecimal(line[2].trim()) : BigDecimal.ZERO;

                    Product product = productRepository.findByCode(code).orElse(new Product());
                    product.setCode(code);
                    product.setName(name);
                    product.setWeight(weight);
                    productRepository.save(product);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + totalProcessed + ": " + e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            errors.add("Failed to read CSV: " + e.getMessage());
        }

        return ImportResultDTO.builder()
                .success(errors.isEmpty())
                .importedCount(successCount)
                .skippedCount(errors.size())
                .errors(errors)
                .build();
    }

    public ImportResultDTO importInventoryCsv(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int totalProcessed = 0;
        int successCount = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] header = reader.readNext();
            if (header == null) {
                return ImportResultDTO.builder()
                        .success(false).importedCount(0).skippedCount(1).errors(List.of("Empty CSV file"))
                        .build();
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                totalProcessed++;
                try {
                    if (line.length < 3) {
                        errors.add("Row " + totalProcessed + ": insufficient columns");
                        continue;
                    }
                    String productCode = line[0].trim();
                    String location = line[1].trim();
                    int quantity = Integer.parseInt(line[2].trim());

                    Product product = productRepository.findByCode(productCode)
                            .orElseThrow(() -> new RuntimeException("Product not found: " + productCode));

                    Inventory inventory = inventoryRepository.findByProductAndLocation(product, location)
                            .orElse(Inventory.builder()
                                    .product(product)
                                    .location(location)
                                    .quantity(0)
                                    .build());
                    inventory.setQuantity(quantity);
                    inventoryRepository.save(inventory);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + totalProcessed + ": " + e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            errors.add("Failed to read CSV: " + e.getMessage());
        }

        return ImportResultDTO.builder()
                .success(errors.isEmpty())
                .importedCount(successCount)
                .skippedCount(errors.size())
                .errors(errors)
                .build();
    }
}
