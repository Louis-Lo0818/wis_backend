package wis.my_spring_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDTO {
    private Long totalProducts;
    private Long totalLocations;
    private Long totalInventoryRecords;
    private Integer totalQuantity;
}
