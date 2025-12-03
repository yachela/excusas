package davinci.edu.ar.excusasSA.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainLineDTO {
    
    private Long id; // Null al crear, lleno al responder

    @NotBlank(message = "The chain code name cannot be empty")
    private String nameCode;
}