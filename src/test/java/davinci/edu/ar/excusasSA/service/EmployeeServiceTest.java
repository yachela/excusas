package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.dto.EmployeeDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.AreaSupervisor;
import davinci.edu.ar.excusasSA.model.employee.incharge.CEO;
import davinci.edu.ar.excusasSA.model.employee.incharge.HumanResourcesManager;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    // Datos base para DTOs
    private final Long TEST_ID = 1L;
    private final Long TEST_LEGAJO = 1001L;
    private final String TEST_NAME = "Juan Perez";
    private final String TEST_EMAIL = "juan@example.com";

    // Un mock genérico de Employee
    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        // Inicializamos el mockEmployee antes de cada test
        mockEmployee = mock(Employee.class);
    }

    // --- getEmployeeById(Long id) ---

    @Test
    void getEmployeeById_Success_ShouldReturnEmployee() {
        // Arrange
        when(employeeRepository.findById(TEST_ID)).thenReturn(Optional.of(mockEmployee));

        // Act
        Employee result = employeeService.getEmployeeById(TEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(mockEmployee, result);
        verify(employeeRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void getEmployeeById_NotFound_ShouldThrowNoSuchElementException() {
        // Arrange
        when(employeeRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            employeeService.getEmployeeById(TEST_ID);
        });

        verify(employeeRepository, times(1)).findById(TEST_ID);
    }

    // --- getAllEmployees() ---

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() {
        // Arrange
        List<Employee> expectedList = Arrays.asList(mockEmployee, mock(CEO.class));
        when(employeeRepository.findAll()).thenReturn(expectedList);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(employeeRepository, times(1)).findAll();
    }

    // --- createEmployee(EmployeeDTO dto) - Instanciación Polimórfica ---

    @Test
    void createEmployee_ValidRoleCEO_ShouldSaveCEOEntity() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO(null, TEST_NAME, TEST_EMAIL, TEST_LEGAJO, "CEO");

        // Capturamos la entidad antes de que se guarde para verificar su tipo y atributos
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);

        // Simular el guardado: devuelve el mismo objeto que recibió
        when(employeeRepository.save(employeeCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee savedEmployee = employeeService.createEmployee(dto);

        // Assert
        // 1. Verifica que el tipo de instancia creado sea correcto
        assertTrue(savedEmployee instanceof CEO);

        // 2. Verifica que se llamó al repositorio con el tipo correcto (CEO)
        verify(employeeRepository, times(1)).save(any(CEO.class));

        // 3. Verifica los datos del objeto capturado
        CEO capturedCEO = (CEO) employeeCaptor.getValue();
        assertEquals(TEST_LEGAJO, capturedCEO.getLegajo());
        assertEquals(TEST_NAME, capturedCEO.getName());
    }

    @Test
    void createEmployee_ValidRoleAreaSupervisor_ShouldSaveAreaSupervisorEntity() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO(null, TEST_NAME, TEST_EMAIL, TEST_LEGAJO, "AreaSupervisor");

        // Simular el guardado: devuelve el mismo objeto que recibió
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee savedEmployee = employeeService.createEmployee(dto);

        // Assert
        assertTrue(savedEmployee instanceof AreaSupervisor);
        verify(employeeRepository, times(1)).save(any(AreaSupervisor.class));
    }

    @Test
    void createEmployee_ValidRoleHumanResourcesManager_ShouldSaveHRMEntity() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO(null, TEST_NAME, TEST_EMAIL, TEST_LEGAJO, "HumanResourcesManager");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee savedEmployee = employeeService.createEmployee(dto);

        // Assert
        assertTrue(savedEmployee instanceof HumanResourcesManager);
        verify(employeeRepository, times(1)).save(any(HumanResourcesManager.class));
    }

    @Test
    void createEmployee_ValidRoleReceptionist_ShouldSaveReceptionistEntity() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO(null, TEST_NAME, TEST_EMAIL, TEST_LEGAJO, "Receptionist");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee savedEmployee = employeeService.createEmployee(dto);

        // Assert
        assertTrue(savedEmployee instanceof Receptionist);
        verify(employeeRepository, times(1)).save(any(Receptionist.class));
    }

    // --- createEmployee(EmployeeDTO dto) - Manejo de Errores ---

    @Test
    void createEmployee_InvalidRole_ShouldThrowIllegalArgumentException() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO(null, TEST_NAME, TEST_EMAIL, TEST_LEGAJO, "INVALID_ROLE");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(dto);
        });

        // Verifica el mensaje de la excepción de rol no válido
        assertTrue(thrown.getMessage().contains("no es un rol de empleado válido"));

        // Verifica que NUNCA se haya llamado al repositorio para guardar
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_RoleLowerCase_ShouldSucceed() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO(null, TEST_NAME, TEST_EMAIL, TEST_LEGAJO, "ceo"); // Rol en minúsculas

        // Simular el guardado y devolver la instancia correcta
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Employee savedEmployee = employeeService.createEmployee(dto);

        // Assert
        // Debe ser capaz de manejar mayúsculas/minúsculas debido al `toUpperCase()` en el servicio
        assertTrue(savedEmployee instanceof CEO);
        verify(employeeRepository, times(1)).save(any(CEO.class));
    }
}