package davinci.edu.ar.excusasSA.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcuseDTO {
    private Long id;
    private Long employeeId;
    @NotBlank(message = "Type Excuse cannot be blank")
    private String typeExcuseName;
    @NotBlank(message = "Register Date cannot be blank")
    private LocalDate RegisterDate;
    @NotBlank(message = "Status cannot be blank")
    private String status;
}