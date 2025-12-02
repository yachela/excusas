package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.ExcuseDTO;
import davinci.edu.ar.excusasSA.factory.LineInChargeFactory;
import davinci.edu.ar.excusasSA.factory.TypeExcuseFactory;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.Handler;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.repository.ExcuseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcuseServiceTest {

    // --- Mocks de Dependencias ---
    @Mock
    private ExcuseRepository excuseRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TypeExcuseFactory typeExcuseFactory;

    @Mock
    private LineInChargeFactory lineInChargeFactory;

    // --- Inyección del Servicio a probar ---
    @InjectMocks
    private ExcuseService excuseService;

    // --- Datos de Prueba ---
    private static final Long TEST_LEGAJO = 98765L;
    private static final String TEST_TYPE_NAME = "TRIVIAL";
    private static final String DEFAULT_CHAIN_ID = "DEFAULT";
    private ExcuseDTO testDto;
    private Employee mockEmployee;
    private TypeExcuse mockTypeExcuse;
    private Handler mockChainHead;
    private Excuse mockExcuse;

    @BeforeEach
    void setUp() {
        // Inicializar DTO
        testDto = new ExcuseDTO(null, null, null, TEST_LEGAJO, TEST_TYPE_NAME);

        // Inicializar Mocks de Entidades y Componentes
        mockEmployee = mock(Employee.class);
        mockTypeExcuse = mock(TypeExcuse.class);
        mockChainHead = mock(Handler.class);
        mockExcuse = mock(Excuse.class); // Mockeamos la excusa para controlar su estado
    }

    // ====================================================================
    // 1. registerAndProcess()
    // ====================================================================

    @Test
    void registerAndProcess_Success() {
        // Arrange
        // FIX: Se debe estubbear getRandomChainId() para que retorne un valor no nulo
        when(lineInChargeFactory.getRandomChainId()).thenReturn(DEFAULT_CHAIN_ID);

        when(employeeRepository.findByLegajo(TEST_LEGAJO)).thenReturn(Optional.of(mockEmployee));
        when(typeExcuseFactory.createTypeExcuse(TEST_TYPE_NAME)).thenReturn(mockTypeExcuse);
        when(mockEmployee.generateExcuse(mockTypeExcuse)).thenReturn(mockExcuse);
        when(mockExcuse.getStatus()).thenReturn(ExcuseStatus.Accepted);
        when(lineInChargeFactory.getChainHead(DEFAULT_CHAIN_ID)).thenReturn(mockChainHead);
        when(excuseRepository.save(mockExcuse)).thenReturn(mockExcuse);

        // Act
        Excuse result = excuseService.registerAndProcess(testDto);

        // Assert
        assertNotNull(result);
        assertEquals(ExcuseStatus.Accepted, result.getStatus()); // Verifica el estado final (mockeado)

        // Verificar el flujo de ejecución:
        verify(employeeRepository, times(1)).findByLegajo(TEST_LEGAJO);
        verify(typeExcuseFactory, times(1)).createTypeExcuse(TEST_TYPE_NAME);
        verify(mockEmployee, times(1)).generateExcuse(mockTypeExcuse);
        verify(lineInChargeFactory, times(1)).getRandomChainId(); // Nueva verificación
        verify(lineInChargeFactory, times(1)).getChainHead(DEFAULT_CHAIN_ID);
        verify(mockChainHead, times(1)).handlerExcuse(mockExcuse); // Verifica que se ejecutó la cadena
        verify(excuseRepository, times(1)).save(mockExcuse); // Verifica que se persistió
    }

    @Test
    void registerAndProcess_EmployeeNotFound_ShouldThrowNoSuchElementException() {
        // Arrange
        when(employeeRepository.findByLegajo(TEST_LEGAJO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            excuseService.registerAndProcess(testDto);
        });

        // Verificar que solo se intentó buscar el empleado
        verify(employeeRepository, times(1)).findByLegajo(TEST_LEGAJO);
        verify(typeExcuseFactory, never()).createTypeExcuse(any());
        verify(lineInChargeFactory, never()).getChainHead(any());
        verify(excuseRepository, never()).save(any());
    }

    // El test para TypeExcuseFactory falla se asume que se manejaría con una excepción
    // lanzada por la fábrica, que tu controlador debería capturar como 400 Bad Request.


    // ====================================================================
    // 2. getExcusesByEmployeeLegajo()
    // ====================================================================

    @Test
    void getExcusesByEmployeeLegajo_ShouldReturnList() {
        // Arrange
        List<Excuse> expectedList = Arrays.asList(mockExcuse, new Excuse());
        when(excuseRepository.findByEmployee_Legajo(TEST_LEGAJO)).thenReturn(expectedList);

        // Act
        List<Excuse> result = excuseService.getExcusesByEmployeeLegajo(TEST_LEGAJO);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedList, result);

        verify(excuseRepository, times(1)).findByEmployee_Legajo(TEST_LEGAJO);
    }

    // ====================================================================
    // 3. getRejectedExcuses()
    // ====================================================================

    @Test
    void getRejectedExcuses_ShouldReturnDeniedList() {
        // Arrange
        List<Excuse> expectedList = List.of(mockExcuse);
        when(excuseRepository.findByStatus(ExcuseStatus.Denied)).thenReturn(expectedList);

        // Act
        List<Excuse> result = excuseService.getRejectedExcuses();

        // Assert
        assertEquals(1, result.size());
        verify(excuseRepository, times(1)).findByStatus(ExcuseStatus.Denied);
    }

    // ====================================================================
    // 4. deleteExcusesBeforeDate()
    // ====================================================================

    @Test
    void deleteExcusesBeforeDate_ShouldReturnDeletedCount() {
        // Arrange
        LocalDate limitDate = LocalDate.of(2024, 1, 1);
        int deletedCount = 5;
        when(excuseRepository.deleteByRegisterDateBefore(limitDate)).thenReturn(deletedCount);

        // Act
        int result = excuseService.deleteExcusesBeforeDate(limitDate);

        // Assert
        assertEquals(deletedCount, result);

        verify(excuseRepository, times(1)).deleteByRegisterDateBefore(limitDate);
    }

    // ====================================================================
    // 5. getAllExcusesWithFilters()
    // ====================================================================

    @Test
    void getAllExcusesWithFilters_WithAllFilters_ShouldCallRepositoryWithEnums() {
        // Arrange
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        LocalDate dateUntil = LocalDate.of(2024, 1, 31);
        String statusString = "Accepted";
        ExcuseStatus expectedStatus = ExcuseStatus.Accepted;
        when(excuseRepository.findFilteredSimple(dateFrom, dateUntil, expectedStatus)).thenReturn(List.of(mockExcuse));
        // Act
        List<Excuse> result = excuseService.getAllExcusesWithFilters(dateFrom, dateUntil, statusString);

        // Assert
        assertEquals(1, result.size());
        verify(excuseRepository, times(1)).findFilteredSimple(dateFrom, dateUntil, expectedStatus);
    }

    @Test
    void getAllExcusesWithFilters_WithNullFilters_ShouldCallRepositoryWithNulls() {
        // Arrange
        when(excuseRepository.findFilteredSimple(null, null, null)).thenReturn(List.of(mockExcuse));
        // Act
        List<Excuse> result = excuseService.getAllExcusesWithFilters(null, null, null);
        // Assert
        assertEquals(1, result.size());
        verify(excuseRepository, times(1)).findFilteredSimple(null, null, null);
    }

    @Test
    void getAllExcusesWithFilters_WithInvalidStatus_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidStatus = "INVALIDO";
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            excuseService.getAllExcusesWithFilters(null, null, invalidStatus);
        });
        // Assert: El repositorio no debe ser llamado
        verify(excuseRepository, never()).findFilteredSimple(any(), any(), any());
    }
}