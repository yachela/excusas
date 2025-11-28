package davinci.edu.ar.excusasSA.model.excuse;

public interface IExcuse {
    boolean isTrivial();
    boolean isImplausible();
    boolean isComplex();
    boolean isModerate();
}
