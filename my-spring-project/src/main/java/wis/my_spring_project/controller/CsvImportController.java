package wis.my_spring_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wis.my_spring_project.dto.ImportResultDTO;
import wis.my_spring_project.service.CsvImportService;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin
public class CsvImportController {

    private final CsvImportService csvImportService;

    @PostMapping("/products")
    public ResponseEntity<ImportResultDTO> importProductsCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvImportService.importProductsCsv(file));
    }

    @PostMapping("/inventory")
    public ResponseEntity<ImportResultDTO> importInventoryCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvImportService.importInventoryCsv(file));
    }
}
