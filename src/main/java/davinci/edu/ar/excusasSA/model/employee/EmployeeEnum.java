package davinci.edu.ar.excusasSA.model.employee;

import davinci.edu.ar.excusasSA.model.employee.incharge.AreaSupervisor;
import davinci.edu.ar.excusasSA.model.employee.incharge.CEO;
import davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager;
import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;

/**
 * Enum que define los roles de empleados que pueden ser eslabones en la cadena.
 * Contiene la lógica para instanciar la clase concreta del encargado (Enum-Driven Factory).
 */
public enum EmployeeEnum {
    CEO {
        @Override
        public InCharge createInstance(String name, String email, Long legajo, Strategy strategy) {
            return new CEO(name, email, legajo, strategy);
        }
    },
    AREASUPERVISOR {
        @Override
        public InCharge createInstance(String name, String email, Long legajo, Strategy strategy) {
            return new AreaSupervisor(name, email, legajo, strategy);
        }
    },
    HUMANRESOURCESMANAGER {
        @Override
        public InCharge createInstance(String name, String email, Long legajo, Strategy strategy) {
            return new HumanResourcesManager(name, email, legajo, strategy);
        }
    },
    RECEPTIONIST {
        @Override
        public InCharge createInstance(String name, String email, Long legajo, Strategy strategy) {
            return new Receptionist(name, email, legajo, strategy);
        }
    };
    // Método abstracto que cada constante debe implementar para crear su respectiva clase.
    public abstract InCharge createInstance(String name, String email, Long legajo, Strategy strategy);
}