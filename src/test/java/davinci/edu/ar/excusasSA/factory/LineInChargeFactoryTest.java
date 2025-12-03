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
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineInChargeFactoryTest {

    @Mock
    private LineInChargeRepository lineInChargeRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private TransactionTemplate transactionTemplate; // 1. Nuevo Mock necesario

    private LineInChargeFactory lineInChargeFactory;

    private final String CHAIN_ID_HR = "HR_DEPT";
    private final String CHAIN_ID_FINANCE = "FINANCE";
    private final Long RECEPTIONIST_LEGAJO = 100L;
    private final Long CEO_LEGAJO = 200L;

    @BeforeEach
    void setUp() {
        // 2. Configurar el TransactionTemplate para que ejecute el callback inmediatamente
        // Esto simula que la transacción se abre y se ejecuta el código de carga
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });

        // Configuración de mocks de repositorios (Caso feliz por defecto)
        lenient().when(lineInChargeRepository.findAllDistinctChainIds())
                .thenReturn(Arrays.asList(CHAIN_ID_HR, CHAIN_ID_FINANCE));

        lenient().when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_HR))
                .thenReturn(createMockHRConfigs());

        lenient().when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_FINANCE))
                .thenReturn(createMockFinanceConfigs());

        lenient().when(employeeRepository.findByLegajo(RECEPTIONIST_LEGAJO))
                .thenReturn(Optional.of(createMockReceptionistEmployee()));
        lenient().when(employeeRepository.findByLegajo(CEO_LEGAJO))
                .thenReturn(Optional.of(createMockCEOEmployee()));

        // 3. Inicialización con el nuevo constructor (4 parámetros)
        lineInChargeFactory = new LineInChargeFactory(
                lineInChargeRepository,
                employeeRepository,
                emailSenderService,
                transactionTemplate
        );
        
        // 4. Llamada manual a init() porque @PostConstruct no corre solo en tests unitarios
        lineInChargeFactory.init();
    }

    // ... Métodos de soporte (createMockHRConfigs, etc) se mantienen igual ...
    private List<LineInCharge> createMockHRConfigs() {
        Employee receptionist = new Receptionist(null, null, RECEPTIONIST_LEGAJO, null);
        LineInCharge config1 = new LineInCharge(1L, new ChainLine(10L, CHAIN_ID_HR), receptionist, 1, "NORMAL");
        Employee ceo = new CEO(null, null, CEO_LEGAJO, null);
        LineInCharge config2 = new LineInCharge(2L, new ChainLine(10L, CHAIN_ID_HR), ceo, 2, "LAZY");
        return Arrays.asList(config1, config2);
    }

    private List<LineInCharge> createMockFinanceConfigs() {
        Employee receptionist = new Receptionist(null, null, RECEPTIONIST_LEGAJO, null);
        LineInCharge config1 = new LineInCharge(3L, new ChainLine(20L, CHAIN_ID_FINANCE), receptionist, 1, "NORMAL");
        return Collections.singletonList(config1);
    }

    private Employee createMockReceptionistEmployee() {
        return new Receptionist("Jane Doe", "jane.doe@hr.com", RECEPTIONIST_LEGAJO, null);
    }

    private Employee createMockCEOEmployee() {
        return new CEO("John Smith", "john.smith@sa.com", CEO_LEGAJO, null);
    }

    // ====================================================================
    // TESTS
    // ====================================================================

    @Test
    void getChainHead_shouldReturnCorrectlyBuiltChain_forHR() {
        Handler head = lineInChargeFactory.getChainHead(CHAIN_ID_HR);

        assertNotNull(head);
        assertTrue(head instanceof Receptionist);

        Receptionist receptionist = (Receptionist) head;
        assertTrue(receptionist.getStrategy() instanceof Normal);
        assertEquals(CEO_LEGAJO, ((Employee) receptionist.getNext()).getLegajo());

        CEO ceo = (CEO) receptionist.getNext();
        assertTrue(ceo.getStrategy() instanceof Lazy);

        Handler specialManager = ceo.getNext();
        assertEquals(999L, ((Employee) specialManager).getLegajo());
        assertTrue(specialManager instanceof SpecialManager);
    }

    @Test
    void getChainHead_shouldReturnSingleLinkChain_forFINANCE() {
        Handler head = lineInChargeFactory.getChainHead(CHAIN_ID_FINANCE);

        assertNotNull(head);
        assertTrue(head instanceof Receptionist);

        Receptionist receptionist = (Receptionist) head;
        assertTrue(receptionist.getStrategy() instanceof Normal);

        Handler specialManager = receptionist.getNext();
        assertEquals(999L, ((Employee) specialManager).getLegajo());
    }

    @Test
    void getChainHead_shouldReturnOnlySpecialManager_whenConfigsAreEmpty() {
        final String EMPTY_CHAIN = "EMPTY_TEST";

        // Reconfigurar mocks para este caso específico
        when(lineInChargeRepository.findAllDistinctChainIds())
                .thenReturn(Arrays.asList(CHAIN_ID_HR, CHAIN_ID_FINANCE, EMPTY_CHAIN));
        when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(EMPTY_CHAIN))
                .thenReturn(Collections.emptyList());

        // Re-crear e inicializar la factory para cargar la nueva cadena vacía
        LineInChargeFactory factory = new LineInChargeFactory(
                lineInChargeRepository,
                employeeRepository,
                emailSenderService,
                transactionTemplate
        );
        factory.init();

        Handler head = factory.getChainHead(EMPTY_CHAIN);

        assertTrue(head instanceof SpecialManager);
        assertEquals(999L, ((Employee) head).getLegajo());
        assertNull(((InCharge) head).getNext());
    }

    @Test
    void getRandomChainId_shouldReturnOneOfTheLoadedIds() {
        String randomId = lineInChargeFactory.getRandomChainId();
        assertTrue(Arrays.asList(CHAIN_ID_HR, CHAIN_ID_FINANCE).contains(randomId));
    }

    @Test
    void getChainHead_shouldThrowException_whenChainIdDoesNotExist() {
        final String NON_EXISTENT_CHAIN = "NON_EXISTENT";
        assertThrows(IllegalArgumentException.class, () -> lineInChargeFactory.getChainHead(NON_EXISTENT_CHAIN));
    }

    @Test
    void rebuildChain_shouldReloadSpecificChain() {
        Employee receptionist = new Receptionist(null, null, RECEPTIONIST_LEGAJO, null);
        LineInCharge newConfig = new LineInCharge(1L, new ChainLine(10L, CHAIN_ID_HR), receptionist, 1, "LAZY");
        List<LineInCharge> newHRConfigs = Collections.singletonList(newConfig);

        // Configurar comportamiento para el rebuild
        doReturn(newHRConfigs)
                .when(lineInChargeRepository)
                .findByChainLine_ChainIdCodeOrderByOrderIndexAsc(CHAIN_ID_HR);

        // La llamada a rebuildChain ejecuta lógica, necesitamos asegurar que transactionTemplate ejecute si se usara internamente
        // (aunque en tu código actual rebuildChain usa @Transactional, en tests unitarios llamamos directo al método)
        
        lineInChargeFactory.rebuildChain(CHAIN_ID_HR, lineInChargeRepository, employeeRepository);
        Handler head = lineInChargeFactory.getChainHead(CHAIN_ID_HR);

        assertTrue(head instanceof Receptionist);
        Receptionist receptionistHead = (Receptionist) head;
        assertTrue(receptionistHead.getStrategy() instanceof Lazy);
        assertEquals(999L, ((Employee) receptionistHead.getNext()).getLegajo());
    }

    @Test
    void init_shouldThrowException_whenEmployeeNotFound() {
        final String ERROR_CHAIN = "ERROR_TEST";
        final Long NON_EXISTENT_LEGAJO = 555L;

        // Configuración específica de error
        when(lineInChargeRepository.findAllDistinctChainIds())
                .thenReturn(Collections.singletonList(ERROR_CHAIN));
        when(lineInChargeRepository.findByChainLine_ChainIdCodeOrderByOrderIndexAsc(ERROR_CHAIN))
                .thenReturn(Collections.singletonList(
                        new LineInCharge(1L, new ChainLine(30L, ERROR_CHAIN), new Receptionist(null, null, NON_EXISTENT_LEGAJO, null), 1, "NORMAL")
                ));
        when(employeeRepository.findByLegajo(NON_EXISTENT_LEGAJO))
                .thenReturn(Optional.empty());

        // Instanciar nueva factory
        LineInChargeFactory factory = new LineInChargeFactory(
                lineInChargeRepository,
                employeeRepository,
                emailSenderService,
                transactionTemplate
        );

        // Ahora el error salta al llamar a init(), no en el constructor
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () -> factory.init());

        assertTrue(thrown.getMessage().contains("Person in charge with file " + NON_EXISTENT_LEGAJO + " not found."));
    }
}