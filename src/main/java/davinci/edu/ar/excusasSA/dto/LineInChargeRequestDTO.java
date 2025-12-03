package davinci.edu.ar.excusasSA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineInChargeRequestDTO {
    private Long chainLineId;
    private Long employeeId;
    private Integer orderIndex;
    private String strategyMode;
}