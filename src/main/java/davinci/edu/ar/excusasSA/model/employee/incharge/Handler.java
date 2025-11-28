package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.excuse.Excuse;

public interface Handler {
    void setHandler(Handler handler);
    void handlerExcuse(Excuse excuse);
}
