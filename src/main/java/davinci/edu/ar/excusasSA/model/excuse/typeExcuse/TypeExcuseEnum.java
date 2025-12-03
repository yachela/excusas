package davinci.edu.ar.excusasSA.model.excuse.typeExcuse;

import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl.FamilyEmergency;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.impl.PowerOutage;

/**
 * Enum que define los tipos de excusas.
 * Contiene la lógica para instanciar la clase concreta de la excusa (Enum-Driven Factory).
 */
public enum TypeExcuseEnum {

    TRIVIAL {
        @Override
        public TypeExcuse createExcuseInstance() {
            return new TrivialExcuse();
        }
    },
    COMPLEX {
        @Override
        public TypeExcuse createExcuseInstance() {
            return new ComplexExcuse();
        }
    },
    IMPLAUSIBLE {
        @Override
        public TypeExcuse createExcuseInstance() {
            return new ImplausibleExcuse();
        }
    },
    // Las constantes deben coincidir con los strings que esperas de entrada (Ej: POWER_OUTAGE)
    POWEROUTAGE {
        @Override
        public TypeExcuse createExcuseInstance() {
            return new PowerOutage();
        }
    },
    FAMILYEMERGENCY {
        @Override
        public TypeExcuse createExcuseInstance() {
            return new FamilyEmergency();
        }
    };

    /**
     * Método abstracto que cada constante debe implementar para crear su respectiva subclase de TypeExcuse.
     */
    public abstract TypeExcuse createExcuseInstance();
}