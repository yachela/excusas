package davinci.edu.ar.excusasSA.factory;

import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuseEnum; // Importamos el Enum
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TypeExcuseFactory {

    /** Crea la subclase de TypeExcuse adecuada basándose en el motivo de entrada (delegando al Enum). */
    public TypeExcuse createTypeExcuse(String motiveString) {
        if (motiveString == null || motiveString.trim().isEmpty()) {
            throw new IllegalArgumentException("The reason for the excuse cannot be empty.");
        }
        String normalizedMotive = motiveString.trim().toUpperCase(Locale.ROOT);
        try {
            TypeExcuseEnum excuseType = TypeExcuseEnum.valueOf(normalizedMotive);
            return excuseType.createExcuseInstance();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid or unsupported reason for excuse: " + motiveString, e);
        }
    }
}