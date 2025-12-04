package davinci.edu.ar.excusasSA.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LineInChargeDTO {

    private Long id;

    @NotNull(message = "Employee Legajo is mandatory.")
    @Positive(message = "Employee Legajo must be a positive number.")
    private Long employeeLegajo;

    @NotBlank(message = "Chain ID Code is mandatory.")
    private String chainIdCode;

    @NotNull(message = "Order Index is mandatory.")
    @Positive(message = "Order Index must be a positive number.")
    private Integer orderIndex;

    @NotBlank(message = "Strategy Mode is mandatory.")
    private String strategyMode;
}