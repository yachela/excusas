package davinci.edu.ar.excusasSA.model;

import davinci.edu.ar.excusasSA.model.inchargers.InCharge;
import davinci.edu.ar.excusasSA.model.strategy.Normal;

public class HumanResourcesManager extends InCharge {
    public HumanResourcesManager(String rrhh, String mail, long l, Normal normal) {
        super();
    }

    @Override
    protected boolean canHandleExcuse(Excuse excuse) {
        return false;
    }
}
