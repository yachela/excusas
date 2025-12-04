package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.LineInChargeDTO;
import davinci.edu.ar.excusasSA.factory.LineInChargeFactory;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.repository.ChainLineRepository;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.LineInChargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineInChargeServiceTest {

    @Mock
    private LineInChargeRepository lineInChargeRepository;

    @Mock
    private ChainLineRepository chainLineRepository;

    @Mock
    private LineInChargeFactory lineInChargeFactory;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private LineInChargeService lineInChargeService;

    // Elementos de Entrada/Salida
    private LineInChargeDTO mockInputDTO; // Nuevo objeto de entrada
    private LineInCharge mockSavedConfig; // Objeto de salida (Entidad)
    private ChainLine persistentChainLine; // ChainLine ya guardado
    private Employee mockEmployee; // Empleado encontrado por el Legajo

    private final String CHAIN_CODE = "HR_DEPT";
    private final Long LEGAJO = 600L;
    private final String STRATEGY_PRODUCTIVE = "PRODUCTIVE";
    private final String STRATEGY_LAZY = "LAZY";
    private final Long CONFIG_ID = 1L;
    private final Long EMPLOYEE_ID = 100L;
    private final Long NON_EXISTENT_LEGAJO = 999L;

    @BeforeEach
    void setUp() {
        // --- 1. DTO de Entrada (Lo que recibe el Service) ---
        mockInputDTO = new LineInChargeDTO(
                null,
                LEGAJO,
                CHAIN_CODE,
                2,
                STRATEGY_PRODUCTIVE
        );

        // --- 2. Objetos de Dependencia (Lo que los Repositories deben devolver) ---

        // ChainLine que simula estar en la BD (con ID)
        persistentChainLine = new ChainLine(CONFIG_ID, CHAIN_CODE);

        // Objeto Employee (ya persistido y con ID)
        mockEmployee = new Receptionist("Test Name", "test@sa.com", LEGAJO, null);
        mockEmployee.setId(EMPLOYEE_ID);

        // --- 3. Configuración de Línea Guardada (Lo que el Service devuelve) ---
        mockSavedConfig = new LineInCharge(
                CONFIG_ID,
                persistentChainLine,
                mockEmployee,
                2,
                STRATEGY_PRODUCTIVE
        );
    }

    /**
     * Test para createLineConfig: Verifica la creación de la configuración
     * cuando ChainLine y Employee ya existen.
     */
    @Test
    void createLineConfig_whenEmployeeAndChainLineExist_shouldSaveAndRebuildChain() {
        // Arrange
        // 1. Mock: Employee existe (nuevo mock necesario)
        when(employeeRepository.findByLegajo(LEGAJO)).thenReturn(Optional.of(mockEmployee));

        // 2. Mock: ChainLine existe en la BD
        when(chainLineRepository.findByChainIdCode(CHAIN_CODE)).thenReturn(Optional.of(persistentChainLine));

        // 3. Mock: Repository.save devuelve el objeto guardado
        when(lineInChargeRepository.save(any(LineInCharge.class))).thenReturn(mockSavedConfig);

        // Act
        LineInCharge result = lineInChargeService.createLineConfig(mockInputDTO);

        // Assert
        // Verifica que se haya devuelto el objeto guardado
        assertNotNull(result);
        assertEquals(CONFIG_ID, result.getId());
        assertEquals(persistentChainLine, result.getChainLine());
        assertEquals(LEGAJO, result.getEmployee().getLegajo());

        // Verifica las interacciones del repositorio
        verify(employeeRepository, times(1)).findByLegajo(LEGAJO);
        verify(chainLineRepository, never()).save(any(ChainLine.class)); // No se debe guardar un nuevo ChainLine
        verify(lineInChargeRepository, times(1)).save(any(LineInCharge.class)); // Se guarda la LineInCharge

        // Verifica que se reconstruya la cadena de responsabilidad
        verify(lineInChargeFactory, times(1)).rebuildChain(eq(CHAIN_CODE), eq(lineInChargeRepository), eq(employeeRepository));
    }

    /**
     * Test para createLineConfig: Verifica el error si el Employee no se encuentra.
     */
    @Test
    void createLineConfig_whenEmployeeDoesNotExist_shouldThrowException() {
        // Arrange
        LineInChargeDTO dtoWithBadLegajo = new LineInChargeDTO(null, NON_EXISTENT_LEGAJO, CHAIN_CODE, 1, STRATEGY_PRODUCTIVE);

        // 1. Mock: Employee NO existe
        when(employeeRepository.findByLegajo(NON_EXISTENT_LEGAJO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            lineInChargeService.createLineConfig(dtoWithBadLegajo);
        });

        // Verifica que NO se haya intentado buscar/crear ChainLine ni guardar LineInCharge
        verify(chainLineRepository, never()).findByChainIdCode(anyString());
        verify(lineInChargeRepository, never()).save(any(LineInCharge.class));
        verify(lineInChargeFactory, never()).rebuildChain(anyString(), any(), any());
    }

    /**
     * Test para createLineConfig: Verifica la creación de la configuración
     * cuando ChainLine NO existe en la BD (debe crearlo).
     */
    @Test
    void createLineConfig_whenChainLineDoesNotExist_shouldCreateNewChainLine() {
        // Arrange
        // 1. Mock: Employee existe
        when(employeeRepository.findByLegajo(LEGAJO)).thenReturn(Optional.of(mockEmployee));

        // 2. Mock: ChainLine NO existe en la BD
        when(chainLineRepository.findByChainIdCode(CHAIN_CODE)).thenReturn(Optional.empty());

        // 3. Mock: Se simula la creación y guardado del ChainLine
        when(chainLineRepository.save(any(ChainLine.class))).thenReturn(persistentChainLine);

        // 4. Mock: Repository.save devuelve el objeto guardado
        when(lineInChargeRepository.save(any(LineInCharge.class))).thenReturn(mockSavedConfig);

        // Act
        LineInCharge result = lineInChargeService.createLineConfig(mockInputDTO);

        // Assert
        assertNotNull(result);

        // Verifica que se haya llamado a guardar el ChainLine
        verify(chainLineRepository, times(1)).save(any(ChainLine.class));
        verify(lineInChargeRepository, times(1)).save(any(LineInCharge.class));

        // Verifica que se reconstruya la cadena
        verify(lineInChargeFactory, times(1)).rebuildChain(eq(CHAIN_CODE), eq(lineInChargeRepository), eq(employeeRepository));
    }

    /**
     * Test para updateStrategyMode: Actualización exitosa.
     */
    @Test
    void updateStrategyMode_shouldUpdateModeAndRebuildChain() {
        // Arrange
        String newMode = STRATEGY_LAZY;

        // Mock: Simula la búsqueda de la configuración existente
        when(lineInChargeRepository.findByEmployee_LegajoAndChainLine_ChainIdCode(LEGAJO, CHAIN_CODE))
                .thenReturn(Optional.of(mockSavedConfig));

        // Mock: Simula el guardado de la configuración actualizada
        LineInCharge updatedConfig = new LineInCharge(
                CONFIG_ID,
                persistentChainLine,
                mockEmployee,
                2,
                newMode
        );
        when(lineInChargeRepository.save(any(LineInCharge.class))).thenReturn(updatedConfig);

        // Act
        LineInCharge result = lineInChargeService.updateStrategyMode(LEGAJO, CHAIN_CODE, newMode);

        // Assert
        assertNotNull(result);
        assertEquals(newMode, result.getStrategyMode()); // Verifica que el modo se haya actualizado

        // Verifica el guardado y la reconstrucción
        verify(lineInChargeRepository, times(1)).save(mockSavedConfig);
        verify(lineInChargeFactory, times(1)).rebuildChain(eq(CHAIN_CODE), eq(lineInChargeRepository), eq(employeeRepository));
    }

    /**
     * Test para updateStrategyMode: Configuración no encontrada (debe lanzar excepción).
     */
    @Test
    void updateStrategyMode_whenConfigNotFound_shouldThrowException() {
        // Arrange
        // Mock: Simula que no encuentra la configuración
        when(lineInChargeRepository.findByEmployee_LegajoAndChainLine_ChainIdCode(LEGAJO, CHAIN_CODE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            lineInChargeService.updateStrategyMode(LEGAJO, CHAIN_CODE, STRATEGY_LAZY);
        });

        // Verifica que no se haya intentado guardar ni reconstruir nada
        verify(lineInChargeRepository, never()).save(any(LineInCharge.class));
        verify(lineInChargeFactory, never()).rebuildChain(anyString(), any(), any());
    }

    /**
     * Test para getAllLineInCharge: Obtiene la lista completa.
     */
    @Test
    void getAllLineInCharge_shouldReturnAllConfigs() {
        // Arrange
        List<LineInCharge> expectedList = Arrays.asList(mockSavedConfig,
                new LineInCharge(2L, persistentChainLine, mockEmployee, 3, STRATEGY_LAZY));

        when(lineInChargeRepository.findAll()).thenReturn(expectedList);

        // Act
        List<LineInCharge> result = lineInChargeService.getAllLineInCharge();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);

        // Verifica la interacción
        verify(lineInChargeRepository, times(1)).findAll();
    }
}