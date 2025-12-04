package davinci.edu.ar.excusasSA.model.lineincharge;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/* Entidad que representa la línea o grupo de encargados (Ej: "PRODUCCION", "VENTAS").*/
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chain_line")
public class ChainLine {

    // ID autonumérico (Clave Primaria).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador único de la línea (Ej: "PRODUCCION").
    @Column(name = "name_code", nullable = false, unique = true)
    private String chainIdCode;

    public ChainLine(String chainIdCode) {
        this.chainIdCode = chainIdCode;
    }
}