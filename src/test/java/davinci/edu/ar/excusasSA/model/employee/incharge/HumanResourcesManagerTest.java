package davinci.edu.ar.excusasSA.model;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.strategy.Normal;

class HumanResourcesManagerTest extends InCharge {
    public HumanResourcesManagerTest(String rrhh, String mail, long l, Normal normal) {
        super();
    }

    @Override
    protected boolean canHandleExcuse(Excuse excuse) {
        return false;
    }
}
