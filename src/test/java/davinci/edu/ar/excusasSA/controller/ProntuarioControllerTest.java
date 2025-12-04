package davinci.edu.ar.excusasSA.controller;

import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.service.ProntuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProntuarioControllerTest {

    // --- 1. CONFIGURACIÓN DEL TEST PARA REEMPLAZAR BEANS ---
    @TestConfiguration
    static class MockingConfiguration {
        @Bean
        @Primary
        public ProntuarioService mockProntuarioService() {
            return mock(ProntuarioService.class);
        }
    }

    // --- 2. INYECCIÓN DE DEPENDENCIAS ---
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProntuarioService prontuarioService;

    // --- 3. DATOS DE PRUEBA ---
    private Prontuario prontuario1;
    private Prontuario prontuario2;
    private final Long PRONTUARIO_ID_1 = 100L;
    private final Long PRONTUARIO_ID_2 = 101L;

    @BeforeEach
    void setUp() {
        // Mockear entidades anidadas
        Employee mockEmployee = mock(Employee.class);
        when(mockEmployee.getId()).thenReturn(50L);

        Excuse mockExcuse = mock(Excuse.class);
        when(mockExcuse.getId()).thenReturn(500L);

        // Prontuario 1
        prontuario1 = new Prontuario();
        prontuario1.setId(PRONTUARIO_ID_1);
        prontuario1.setEmployee(mockEmployee);
        prontuario1.setExcuse(mockExcuse);

        // Prontuario 2 (con datos ligeramente diferentes, asumiendo que el modelo Prontuario tiene el constructor vacío)
        prontuario2 = new Prontuario();
        prontuario2.setId(PRONTUARIO_ID_2);
        prontuario2.setEmployee(mockEmployee);
        prontuario2.setExcuse(mockExcuse);

        // Limpiar mocks antes de cada prueba
        reset(prontuarioService);
    }

    // ====================================================================
    // 1. GET /prontuarios (Listar todos)
    // ====================================================================
    @Test
    void getProntuarios_shouldReturn200_andListOfProntuarios() throws Exception {
        // Arrange
        List<Prontuario> prontuarios = Arrays.asList(prontuario1, prontuario2);
        when(prontuarioService.getAllProntuarios()).thenReturn(prontuarios);

        // Act & Assert
        mockMvc.perform(get("/prontuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(PRONTUARIO_ID_1.intValue())))
                .andExpect(jsonPath("$[1].id", is(PRONTUARIO_ID_2.intValue())))
                .andExpect(jsonPath("$[0].employeeId", is(prontuario1.getEmployee().getId().intValue())));

        verify(prontuarioService, times(1)).getAllProntuarios();
    }

    // ====================================================================
    // 2. GET /prontuarios/{id} (Búsqueda por ID) - Éxito
    // ====================================================================
    @Test
    void getProntuarioById_shouldReturn200_andProntuario() throws Exception {
        // Arrange
        when(prontuarioService.getProntuarioById(PRONTUARIO_ID_1)).thenReturn(prontuario1);

        // Act & Assert
        mockMvc.perform(get("/prontuarios/{id}", PRONTUARIO_ID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(PRONTUARIO_ID_1.intValue())))
                .andExpect(jsonPath("$.excuseId", is(prontuario1.getExcuse().getId().intValue())));

        verify(prontuarioService, times(1)).getProntuarioById(PRONTUARIO_ID_1);
    }

    // ====================================================================
    // 2. GET /prontuarios/{id} (Búsqueda por ID) - No encontrado
    // ====================================================================
    @Test
    void getProntuarioById_shouldReturn404_whenProntuarioNotFound() throws Exception {
        // Arrange
        Long nonExistentId = 999L;
        // Simula la excepción que se lanzaría si no se encuentra el prontuario
        when(prontuarioService.getProntuarioById(nonExistentId)).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(get("/prontuarios/{id}", nonExistentId))
                .andExpect(status().isNotFound()) // Verifica la respuesta 404
                .andExpect(content().string("")); // El cuerpo de la respuesta debería estar vacío si devuelve .build()

        verify(prontuarioService, times(1)).getProntuarioById(nonExistentId);
    }
}