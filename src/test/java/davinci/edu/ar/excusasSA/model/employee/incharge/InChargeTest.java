package davinci.edu.ar.excusasSA.model.employee.incharge;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InChargeTest {

    // 1. Dependencias Simuladas
    @Mock private EmailSenderService mockEmailSender;
    @Mock private Strategy mockStrategy;
    @Mock private Handler mockNextHandler;
    @Mock private Employee mockEmployee;
    @Mock private TypeExcuse mockTypeExcuse;

    // 2. Objetos a Probar
    private TestInCharge handlerCanHandle;
    private TestInCharge handlerCannotHandle;
    private Excuse spyExcuse; // Usamos Spy en Excuse

    // Clase concreta anidada (se usa Spy en ella en el setUp)
    private static class TestInCharge extends InCharge {
        private final boolean canHandle;

        public TestInCharge(String name, String email, Long legajo, Strategy strategy, boolean canHandle) {
            super(name, email, legajo, strategy);
            this.canHandle = canHandle;
        }

        @Override
        protected boolean canHandleExcuse(Excuse excuse) {
            return this.canHandle;
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Creamos una instancia real de Excuse y la convertimos en un Spy
        // Esto permite verificar llamadas a executeProcess()
        Excuse realExcuse = new Excuse(mockEmployee, mockTypeExcuse);
        spyExcuse = spy(realExcuse);

        // Handler que SI puede manejar (usamos Spy para verificar llamadas internas a processExcuse)
        handlerCanHandle = spy(new TestInCharge("JefeA", "jefeA@mail.com", 1L, mockStrategy, true));
        handlerCanHandle.configureService(mockEmailSender);

        // Handler que NO puede manejar (usa Spy para verificar que NO llama a processExcuse)
        handlerCannotHandle = spy(new TestInCharge("JefeB", "jefeB@mail.com", 2L, mockStrategy, false));
        handlerCannotHandle.configureService(mockEmailSender);
        handlerCannotHandle.setHandler(mockNextHandler);
    }

    // --- PRUEBAS DEL FLUJO DE CONTROL (handlerExcuse) ---

    /**
     * Prueba el escenario: El InCharge puede manejar la excusa.
     * Debería cambiar el estado a Processed y llamar a processExcuse.
     */
    @Test
    void handlerExcuse_ShouldProcess_WhenCanHandleIsTrue() {
        // Act
        handlerCanHandle.handlerExcuse(spyExcuse);

        // Assert
        // 1. Verifica el cambio de estado.
        assertEquals(ExcuseStatus.Processed, spyExcuse.getStatus(), "La excusa debe ser procesada.");

        // 2. Verifica que se llamó al método interno de procesamiento.
        verify(handlerCanHandle, times(1)).processExcuse(spyExcuse, mockEmailSender);

        // 3. Verifica que la lógica real (executeProcess) fue llamada.
        verify(spyExcuse, times(1)).executeProcess(spyExcuse, mockEmailSender);

        // 4. Verifica que NO se llamó al siguiente eslabón.
        verify(mockNextHandler, never()).handlerExcuse(any(Excuse.class));
    }

    /**
     * Prueba el escenario: El InCharge NO puede manejarla y DELEGA.
     * Debería pasar la excusa al siguiente handler.
     */
    @Test
    void handlerExcuse_ShouldDelegateToNext_WhenCanHandleIsFalse() {
        // Act
        handlerCannotHandle.handlerExcuse(spyExcuse);

        // Assert
        // 1. Verifica que el estado NO se cambió (sigue siendo Pending).
        assertEquals(ExcuseStatus.Pending, spyExcuse.getStatus(), "La excusa debe seguir pendiente.");

        // 2. Verifica que SÍ se llamó al siguiente eslabón.
        verify(mockNextHandler, times(1)).handlerExcuse(spyExcuse);

        // 3. Verifica que el procesamiento NO fue llamado por este handler.
        verify(handlerCannotHandle, never()).processExcuse(any(), any());
    }

    // --- PRUEBA DE INYECCIÓN DE DEPENDENCIAS ---

    /**
     * Prueba que el método configureService inyecta el servicio correctamente,
     * cumpliendo con la Inyección por Método.
     */
    @Test
    void configureService_ShouldInjectEmailSender() {
        // Arrange
        TestInCharge newHandler = new TestInCharge("TestDI", "di@mail.com", 4L, mockStrategy, true);

        // Act: Llamamos al método de inyección (como lo haría Spring)
        newHandler.configureService(mockEmailSender);

        // Assert
        // Verificamos que el campo 'emailSender' contiene el mock inyectado.
        assertEquals(mockEmailSender, newHandler.getEmailSender(), "El EmailSender debe haber sido inyectado.");
    }
}