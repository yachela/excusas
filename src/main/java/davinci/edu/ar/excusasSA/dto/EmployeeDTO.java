package davinci.edu.ar.excusasSA.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "El formato del email es inválido (debe contener '@' y un dominio).")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "El email debe ser un formato estándar: usuario@dominio.com")
    private String email;
    private Long legajo;
    @NotBlank(message = "Role cannot be blank")
    private String role;
}