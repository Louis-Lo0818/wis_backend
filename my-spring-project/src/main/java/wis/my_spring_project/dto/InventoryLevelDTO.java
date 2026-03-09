package wis.my_spring_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryLevelDTO {
    private String productCode;
    private String productName;
    private String location;
    private Integer quantity;
}
