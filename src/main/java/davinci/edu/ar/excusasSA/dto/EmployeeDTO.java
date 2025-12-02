package davinci.edu.ar.excusasSA.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @Id
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "The email format is invalid (it must contain '@' and a domain).")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "The email address must be in a standard format: user@domain.com")
    private String email;
    private Long legajo;
    @NotBlank(message = "Role cannot be blank")
    private String role;
}