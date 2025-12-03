package davinci.edu.ar.excusasSA.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProntuarioDTO {

    private Long id;
    private Long employeeId;
    private Long excuseId;
}