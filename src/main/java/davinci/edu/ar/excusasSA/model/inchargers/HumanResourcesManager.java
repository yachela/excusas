package davinci.edu.ar.excusasSA.model.inchargers;

import davinci.edu.ar.excusasSA.model.Excuse;

public class HumanResourcesManager extends InCharge{
    @Override
    protected boolean canHandleExcuse(Excuse excuse) {
        return false;
    }
}
