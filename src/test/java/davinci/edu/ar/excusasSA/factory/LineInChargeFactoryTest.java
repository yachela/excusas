package davinci.edu.ar.excusasSA.factory;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.CEO;
import davinci.edu.ar.excusasSA.model.employee.incharge.Handler;
import davinci.edu.ar.excusasSA.model.employee.incharge.InCharge;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.employee.incharge.SpecialManager;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.model.strategy.Lazy;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import davinci.edu.ar.excusasSA.repository.LineInChargeRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineInChargeFactoryTest {

    // Dependencias mockeadas de la fábrica
    @Mock
    private LineInChargeRepository lineInChargeRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmailSenderService emailSenderService;

    private LineInChargeFactory lineInChargeFactory;

    // Constantes para las pruebas
    private final String CHAIN_ID_HR = "HR_DEPT";
    private final String CHAIN_ID_FINANCE = "FINANCE";
    private final Long RECEPTIONIST_LEGAJO = 100L;
    private final Long CEO_LEGAJO = 200L;

    @BeforeEach
    void setUp() {
        // La inicialización de la fábrica carga las cadenas, por lo que debemos mockear
        // el comportamiento inicial de los repositorios.

        // Comportamiento del Factory en el constructor (simula la carga de datos)
        when(lineInChargeRepository.findAllDistinctChainIds())
                .thenReturn(Arrays.asList(CHAIN_ID_HR, CHAIN_ID_FINANCE));

        // Configuración para la cadena HR_DEPT (2 eslabones)
        when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_HR))
                .thenReturn(createMockHRConfigs());

        // Configuración para la cadena FINANCE (1 eslabón)
        when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_FINANCE))
                .thenReturn(createMockFinanceConfigs());

        // Configuración de los datos del empleado (lo que devuelve EmployeeRepository)
        when(employeeRepository.findByLegajo(RECEPTIONIST_LEGAJO))
                .thenReturn(Optional.of(createMockReceptionistEmployee()));
        when(employeeRepository.findByLegajo(CEO_LEGAJO))
                .thenReturn(Optional.of(createMockCEOEmployee()));

        // Inicializa la fábrica, lo que llama a loadAllChains()
        lineInChargeFactory = new LineInChargeFactory(
                lineInChargeRepository,
                employeeRepository,
                emailSenderService
        );
    }

    // ====================================================================
    // MÉTODOS DE SOPORTE PARA MOCK DATA
    // ====================================================================

    /** Crea una lista de configuración mockeada para la cadena HR_DEPT (Recepcionista -> CEO) */
    private List<LineInCharge> createMockHRConfigs() {
        // 1. Recepcionista, legajo 100, Strategy: NORMAL
        Employee receptionist = new Receptionist(null, null, RECEPTIONIST_LEGAJO, null);
        LineInCharge config1 = new LineInCharge(1L, new ChainLine(10L, CHAIN_ID_HR), receptionist, 1, "NORMAL");

        // 2. CEO, legajo 200, Strategy: LAZY
        Employee ceo = new CEO(null, null, CEO_LEGAJO, null);
        LineInCharge config2 = new LineInCharge(2L, new ChainLine(10L, CHAIN_ID_HR), ceo, 2, "LAZY");

        return Arrays.asList(config1, config2);
    }

    /** Crea una lista de configuración mockeada para la cadena FINANCE (1 eslabón) */
    private List<LineInCharge> createMockFinanceConfigs() {
        // 1. Recepcionista, legajo 100, Strategy: NORMAL
        Employee receptionist = new Receptionist(null, null, RECEPTIONIST_LEGAJO, null);
        LineInCharge config1 = new LineInCharge(3L, new ChainLine(20L, CHAIN_ID_FINANCE), receptionist, 1, "NORMAL");
        return Collections.singletonList(config1);
    }

    /** Crea una instancia de Employee (Receptionist) con datos completos simulados */
    private Employee createMockReceptionistEmployee() {
        return new Receptionist("Jane Doe", "jane.doe@hr.com", RECEPTIONIST_LEGAJO, null);
    }

    /** Crea una instancia de Employee (CEO) con datos completos simulados */
    private Employee createMockCEOEmployee() {
        return new CEO("John Smith", "john.smith@sa.com", CEO_LEGAJO, null);
    }

    // ====================================================================
    // 1. TESTS DE CONSTRUCCIÓN DE LA CADENA (BUILD)
    // ====================================================================

    /**
     * Verifica que la cadena HR_DEPT se construya correctamente:
     * Recepcionista (Normal) -> CEO (Lazy) -> SpecialManager.
     */
    @Test
    void getChainHead_shouldReturnCorrectlyBuiltChain_forHR() {
        // ACT
        Handler head = lineInChargeFactory.getChainHead(CHAIN_ID_HR);

        // ASSERT
        assertNotNull(head, "La cabeza de la cadena no debe ser nula.");
        assertTrue(head instanceof Receptionist, "La cabeza de la cadena debe ser la Recepcionista.");

        // Primer eslabón: Recepcionista con estrategia Normal
        Receptionist receptionist = (Receptionist) head;
        assertTrue(receptionist.getStrategy() instanceof Normal,
                "El primer eslabón debe tener la estrategia Normal.");

        // CORRECCIÓN: Castear el resultado de getNextHandler() a Employee para usar getLegajo()
        assertEquals(CEO_LEGAJO, ((Employee) receptionist.getNext()).getLegajo(),
                "El Recepcionista debe apuntar al CEO.");

        // Segundo eslabón: CEO con estrategia Lazy
        CEO ceo = (CEO) receptionist.getNext();
        assertTrue(ceo.getStrategy() instanceof Lazy,
                "El CEO debe tener la estrategia Lazy.");

        // Tercer eslabón: SpecialManager
        Handler specialManager = ceo.getNext();
        // CORRECCIÓN: Castear specialManager a Employee para usar getLegajo()
        assertEquals(999L, ((Employee) specialManager).getLegajo(),
                "El último eslabón debe ser el SpecialManager (legajo 999).");

        // Verificación de tipos para asegurar que no haya errores de casting
        assertTrue(specialManager instanceof SpecialManager, "El último eslabón debe ser SpecialManager.");
    }

    /**
     * Verifica que una cadena de un solo eslabón se construya correctamente:
     * Recepcionista (Normal) -> SpecialManager.
     */
    @Test
    void getChainHead_shouldReturnSingleLinkChain_forFINANCE() {
        // ACT
        Handler head = lineInChargeFactory.getChainHead(CHAIN_ID_FINANCE);

        // ASSERT
        assertNotNull(head, "La cabeza de la cadena no debe ser nula.");
        assertTrue(head instanceof Receptionist, "La cabeza debe ser la Recepcionista.");

        // Primer eslabón: Recepcionista
        Receptionist receptionist = (Receptionist) head;
        assertTrue(receptionist.getStrategy() instanceof Normal,
                "El eslabón único debe tener la estrategia Normal.");

        // Segundo eslabón: SpecialManager
        Handler specialManager = receptionist.getNext();
        // CORRECCIÓN: Castear specialManager a Employee para usar getLegajo()
        assertEquals(999L, ((Employee) specialManager).getLegajo(),
                "El eslabón único debe apuntar al SpecialManager.");
    }

    /**
     * Verifica qué sucede si la base de datos devuelve una lista vacía para un chainId.
     * En este caso, debe devolver únicamente el SpecialManager.
     */
    @Test
    void getChainHead_shouldReturnOnlySpecialManager_whenConfigsAreEmpty() {
        // ARRANGE
        final String EMPTY_CHAIN = "EMPTY_TEST";

        // Simula la configuración para un nuevo chainId sin eslabones
        when(lineInChargeRepository.findAllDistinctChainIds())
                .thenReturn(Arrays.asList(CHAIN_ID_HR, CHAIN_ID_FINANCE, EMPTY_CHAIN));
        when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(EMPTY_CHAIN))
                .thenReturn(Collections.emptyList());

        // Creamos una nueva instancia de la fábrica para que re-cargue las cadenas
        LineInChargeFactory factory = new LineInChargeFactory(
                lineInChargeRepository,
                employeeRepository,
                emailSenderService
        );

        // ACT
        Handler head = factory.getChainHead(EMPTY_CHAIN);

        // ASSERT
        // La cabeza debe ser el SpecialManager
        assertTrue(head instanceof SpecialManager,
                "Si la configuración es vacía, la cabeza de la cadena debe ser SpecialManager.");
        // CORRECCIÓN: Castear head a Employee para usar getLegajo()
        assertEquals(999L, ((Employee) head).getLegajo(),
                "El legajo debe ser el del SpecialManager (999).");

        // Dado que SpecialManager hereda de InCharge, podemos acceder a getNextHandler()
        // Cuando SpecialManager es el final de la cadena (o cabeza), no tiene siguiente.
        assertNull(((InCharge) head).getNext(),
                "El SpecialManager, cuando es la cabeza, no debe tener un siguiente eslabón.");
    }

    // ====================================================================
    // 2. TESTS DE FUNCIONALIDAD ADICIONAL
    // ====================================================================

    /**
     * Verifica que la función de selección aleatoria funcione correctamente.
     */
    @Test
    void getRandomChainId_shouldReturnOneOfTheLoadedIds() {
        // ACT
        String randomId = lineInChargeFactory.getRandomChainId();

        // ASSERT
        assertTrue(Arrays.asList(CHAIN_ID_HR, CHAIN_ID_FINANCE).contains(randomId),
                "El ID aleatorio debe ser uno de los IDs cargados.");
    }

    /**
     * Verifica la excepción al intentar obtener una cadena no existente.
     */
    @Test
    void getChainHead_shouldThrowException_whenChainIdDoesNotExist() {
        // ARRANGE
        final String NON_EXISTENT_CHAIN = "NON_EXISTENT";

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> lineInChargeFactory.getChainHead(NON_EXISTENT_CHAIN),
                "Debe lanzar IllegalArgumentException si la cadena no existe en la caché.");
    }

    /**
     * Verifica la reconstrucción de una cadena específica.
     */
    @Test
    void rebuildChain_shouldReloadSpecificChain() {
        // ARRANGE
        // 1. Simular la nueva configuración para HR_DEPT (ahora solo un eslabón)
        Employee receptionist = new Receptionist(null, null, RECEPTIONIST_LEGAJO, null);
        LineInCharge newConfig = new LineInCharge(1L, new ChainLine(10L, CHAIN_ID_HR), receptionist, 1, "LAZY");
        List<LineInCharge> newHRConfigs = Collections.singletonList(newConfig);

        // 2. Mockear el repositorio para devolver la nueva configuración solo al llamar rebuildChain
        // Usamos doReturn().when() para especificar el comportamiento al llamar a rebuildChain
        doReturn(newHRConfigs)
                .when(lineInChargeRepository)
                .findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_HR);

        // 3. Mockear el employeeRepository nuevamente ya que rebuildChain lo llama
        when(employeeRepository.findByLegajo(RECEPTIONIST_LEGAJO))
                .thenReturn(Optional.of(createMockReceptionistEmployee()));


        // ACT
        lineInChargeFactory.rebuildChain(CHAIN_ID_HR, lineInChargeRepository, employeeRepository);
        Handler head = lineInChargeFactory.getChainHead(CHAIN_ID_HR);

        // ASSERT
        // Verifica que la nueva cadena es de un solo eslabón (Recepcionista Lazy -> SpecialManager)
        assertTrue(head instanceof Receptionist, "La cabeza debe seguir siendo Recepcionista.");
        Receptionist receptionistHead = (Receptionist) head;
        assertTrue(receptionistHead.getStrategy() instanceof Lazy,
                "La nueva estrategia debe ser LazyStrategy.");
        // CORRECCIÓN: Castear el resultado de getNextHandler() a Employee para usar getLegajo()
        assertEquals(999L, ((Employee) receptionistHead.getNext()).getLegajo(),
                "El siguiente eslabón debe ser el SpecialManager.");

        // Verificamos que se haya llamado al método de búsqueda de configuración una vez durante el setUp
        // y una vez más durante el rebuildChain.
        verify(lineInChargeRepository, times(2)).findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_HR);
    }

    /**
     * Verifica la excepción al no encontrar el empleado en el repositorio.
     * Esta prueba requiere reiniciar los mocks para evitar la configuración del setUp.
     */
    @Test
    void constructor_shouldThrowException_whenEmployeeNotFound() {
        // ARRANGE
        final String ERROR_CHAIN = "ERROR_TEST";
        final Long NON_EXISTENT_LEGAJO = 555L;

        // Mockear que existe una cadena en el distinctChainIds, pero que tiene un empleado inexistente.
        when(lineInChargeRepository.findAllDistinctChainIds())
                .thenReturn(Collections.singletonList(ERROR_CHAIN)); // Solo cargamos esta cadena para simplificar
        when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(ERROR_CHAIN))
                .thenReturn(Collections.singletonList(
                        new LineInCharge(1L, new ChainLine(30L, ERROR_CHAIN), new Receptionist(null, null, NON_EXISTENT_LEGAJO, null), 1, "NORMAL")
                ));
        // employeeRepository devolverá Optional.empty() para el legajo 555
        when(employeeRepository.findByLegajo(NON_EXISTENT_LEGAJO))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        // La excepción debe ser lanzada durante la inicialización de la fábrica
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> new LineInChargeFactory(
                lineInChargeRepository,
                employeeRepository,
                emailSenderService
        ), "Debe lanzar NoSuchElementException si no se encuentra el empleado configurado.");

        assertTrue(thrown.getMessage().contains("Person in charge with file " + NON_EXISTENT_LEGAJO + " not found."));
    }
}