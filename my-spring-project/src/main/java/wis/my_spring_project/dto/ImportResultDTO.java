package wis.my_spring_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportResultDTO {
    private Boolean success;
    private Integer importedCount;
    private Integer skippedCount;
    private List<String> errors;
}