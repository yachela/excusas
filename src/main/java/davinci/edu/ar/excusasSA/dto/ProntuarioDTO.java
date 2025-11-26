package davinci.edu.ar.excusasSA.dto;
import lombok.Data;
@Data
public class ProntuarioDTO {
    private String employeeName;
    private String fecha;
    private String estadoExcusa;
    private String motivo;

    public ProntuarioDTO(String name, String string, String status, String s) {
    }
}