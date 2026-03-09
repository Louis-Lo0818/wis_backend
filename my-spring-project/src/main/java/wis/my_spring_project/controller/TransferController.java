package wis.my_spring_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wis.my_spring_project.dto.TransferRequestDTO;
import wis.my_spring_project.dto.TransferResponseDTO;
import wis.my_spring_project.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@CrossOrigin
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO request) {
        return ResponseEntity.ok(transferService.transfer(request));
    }
}
