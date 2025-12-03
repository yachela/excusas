package davinci.edu.ar.excusasSA.model.strategy;

/**
 * Enum que define las estrategias de evaluación.
 * Contiene la lógica para instanciar la clase concreta de la estrategia (Enum-Driven Factory).
 */
public enum StrategyEnum {

    LAZY {
        @Override
        public Strategy createStrategyInstance() {
            return new Lazy();
        }
    },
    NORMAL {
        @Override
        public Strategy createStrategyInstance() {
            return new Normal();
        }
    },
    PRODUCTIVE {
        @Override
        public Strategy createStrategyInstance() {
            return new Productive();
        }
    };
    // Método abstracto que cada constante debe implementar para crear su respectiva clase de Estrategia.
    public abstract Strategy createStrategyInstance();
}