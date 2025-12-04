package davinci.edu.ar.excusasSA.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcuseDTO {
    private Long id;
    private String status;
    private LocalDate registerDate;
    @NotNull(message = "Employee Legajo cannot be null")
    private Long employeeLegajo;
    @NotBlank(message = "Type Excuse Name cannot be blank")
    private String typeExcuseName;
}