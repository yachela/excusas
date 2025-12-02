package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.dto.EmployeeDTO;
import davinci.edu.ar.excusasSA.repository.EmployeeRepository;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.service.EmployeeService; // ¡Inyección crucial!
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Usa la configuración de H2
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ¡Inyectamos el servicio para usarlo en el setup (ARRANGE)!
    @Autowired
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        // Aseguramos que la base de datos de prueba esté limpia antes de cada test
        employeeRepository.deleteAll();
    }

    // --- 1. Test POST /employees (Creación exitosa) ---
    @Test
    void createEmployee_shouldReturn201AndSavedEmployee() throws Exception {
        // ARRANGE: DTO de prueba para un CEO
        EmployeeDTO newEmployee = new EmployeeDTO(
                null,
                "Elon Musk",
                "elon@ceo.com",
                9001L,
                "CEO"
        );
        String employeeJson = objectMapper.writeValueAsString(newEmployee);

        // ACT & ASSERT: Simular la llamada POST
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isCreated()) // 201 CREATED
                .andExpect(jsonPath("$.legajo", is(9001)))
                .andExpect(jsonPath("$.role", is("CEO")));
    }

    // --- 2. Test POST /employees (Error de validación - 400 Bad Request) ---
    @Test
    void createEmployee_shouldReturn400_whenEmailIsInvalid() throws Exception {
        // ARRANGE: Email Inválido
        EmployeeDTO invalidEmployee = new EmployeeDTO(
                null,
                "Jane Doe",
                "invalid-email", // ❌ Falla la validación @Email
                9002L,
                "CEO"
        );
        String invalidJson = objectMapper.writeValueAsString(invalidEmployee);

        // ACT & ASSERT
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest()) // 400 BAD REQUEST
                .andExpect(jsonPath("$.email", is("The email format is invalid (it must contain '@' and a domain)."))); // <-- CORRECCIÓN APLICADA AQUÍ
    }

    // --- 3. Test GET /employees (Listar todos) - CORREGIDO ---
    @Test
    void getAllEmployees_shouldReturnListOfEmployees() throws Exception {
        // ARRANGE: Crear dos empleados usando el Servicio para pre-poblar la BD
        EmployeeDTO ceoDTO = new EmployeeDTO(null, "CEO Boss", "boss@corp.com", 100L, "CEO");
        EmployeeDTO receptionistDTO = new EmployeeDTO(null, "Receptionist Ana", "ana@corp.com", 200L, "Receptionist");

        // Usamos el servicio directamente (ARRANGE) para asegurar que los objetos se persistan correctamente.
        employeeService.createEmployee(ceoDTO);
        employeeService.createEmployee(receptionistDTO);

        // ACT & ASSERT: Simular la llamada GET
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$", hasSize(2))) // Verifica que haya 2 elementos
                .andExpect(jsonPath("$[0].legajo", is(100))); // Verifica datos
    }

    // --- 4. Test GET /employees/{id} (Búsqueda por ID Encontrada) - AÑADIDO ---
    @Test
    void getEmployeeById_shouldReturnEmployee_whenFound() throws Exception {
        // ARRANGE: Crear un empleado y obtener el ID asignado por la BD
        EmployeeDTO inputDTO = new EmployeeDTO(null, "Searchable Employee", "find@me.com", 300L, "Receptionist");
        Employee savedEmployee = employeeService.createEmployee(inputDTO);
        Long savedId = savedEmployee.getId();

        // ACT & ASSERT: Buscar por el ID que acabamos de guardar
        mockMvc.perform(get("/employees/" + savedId))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.legajo", is(300)))
                .andExpect(jsonPath("$.role", is("Receptionist")));
    }

    // --- 5. Test GET /employees/{id} (Búsqueda no encontrada - 404 Not Found) ---
    @Test
    void getEmployeeById_shouldReturn404_whenNotFound() throws Exception {
        // ACT & ASSERT: ID que nunca existirá en esta BD limpia
        mockMvc.perform(get("/employees/9999"))
                .andExpect(status().isNotFound()); // 404 NOT FOUND (Manejado por GlobalExceptionHandler)
    }
}