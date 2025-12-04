package davinci.edu.ar.excusasSA.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import davinci.edu.ar.excusasSA.dto.ExcuseDTO;
import davinci.edu.ar.excusasSA.model.employee.Employee;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.excuse.Excuse;
import davinci.edu.ar.excusasSA.model.excuse.ExcuseStatus;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TrivialExcuse;
import davinci.edu.ar.excusasSA.model.excuse.typeExcuse.TypeExcuse;
import davinci.edu.ar.excusasSA.model.strategy.Strategy;
import davinci.edu.ar.excusasSA.service.ExcuseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExcuseControllerTest {

    // --- 1. CONFIGURACIÓN DEL TEST PARA REEMPLAZAR BEANS (Evita @MockBean obsoleto) ---
    @TestConfiguration
    static class MockingConfiguration {
        @Bean
        @Primary
        public ExcuseService mockExcuseService() {
            return mock(ExcuseService.class);
        }

        @Bean
        @Primary
        public Strategy mockStrategy() {
            return mock(Strategy.class);
        }
    }

    // --- 2. INYECCIÓN DE DEPENDENCIAS ---
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExcuseService excuseService;

    @Autowired
    private Strategy mockStrategy;

    // --- 3. DATOS DE PRUEBA ---
    private Employee mockEmployee;
    private TypeExcuse mockTypeExcuse;
    private Excuse approvedExcuse;
    private Excuse rejectedExcuse;
    private final Long employeeLegajo = 12345L;

    @BeforeEach
    void setUp() {
        // Inicialización de mocks y datos
        mockEmployee = new Receptionist("Test Employee", "test@test.com", employeeLegajo, mockStrategy);
        mockTypeExcuse = new TrivialExcuse();

        approvedExcuse = new Excuse(mockEmployee, mockTypeExcuse);
        approvedExcuse.setId(1L);
        approvedExcuse.setRegisterDate(LocalDate.now().minusDays(1));
        approvedExcuse.setStatus(ExcuseStatus.Accepted);

        rejectedExcuse = new Excuse(mockEmployee, mockTypeExcuse);
        rejectedExcuse.setId(2L);
        rejectedExcuse.setRegisterDate(LocalDate.now().minusDays(2));
        rejectedExcuse.setStatus(ExcuseStatus.Denied);

        // Limpiar mocks antes de cada prueba
        reset(excuseService, mockStrategy);
    }

    // ====================================================================
    // 1. POST /excusas (Creación y Proceso)
    // ====================================================================
    @Test
    void registerExcuse_shouldReturn201_andApprovedExcuse() throws Exception {
        ExcuseDTO inputDTO = new ExcuseDTO(
                null, null, null, employeeLegajo, "TrivialExcuse"
        );

        when(excuseService.registerAndProcess(any(ExcuseDTO.class))).thenReturn(approvedExcuse);

        mockMvc.perform(post("/excusas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(approvedExcuse.getId().intValue())))
                .andExpect(jsonPath("$.status", is(ExcuseStatus.Accepted.toString())))
                .andExpect(jsonPath("$.employeeLegajo", is(employeeLegajo.intValue())));

        verify(excuseService, times(1)).registerAndProcess(any(ExcuseDTO.class));
    }

    @Test
    void registerExcuse_shouldReturn400_whenDTOIsInvalid() throws Exception {
        // Legajo nulo, lo cual viola la validación @NotNull
        ExcuseDTO invalidDTO = new ExcuseDTO(null, null, null, null, "TrivialExcuse");

        mockMvc.perform(post("/excusas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.employeeLegajo", is("Employee Legajo cannot be null")));

        verify(excuseService, never()).registerAndProcess(any(ExcuseDTO.class));
    }


    // ====================================================================
    // 2. GET /excusas/{legajo} (Excusas por Empleado)
    // ====================================================================
    @Test
    void getExcusesByEmployeeLegajo_shouldReturn200_andListOfExcuses() throws Exception {
        List<Excuse> excuses = Arrays.asList(approvedExcuse, rejectedExcuse);

        when(excuseService.getExcusesByEmployeeLegajo(employeeLegajo)).thenReturn(excuses);

        mockMvc.perform(get("/excusas/{legajo}", employeeLegajo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].employeeLegajo", is(employeeLegajo.intValue())))
                .andExpect(jsonPath("$[1].status", is(ExcuseStatus.Denied.toString())));

        verify(excuseService, times(1)).getExcusesByEmployeeLegajo(employeeLegajo);
    }


    // ====================================================================
    // 3. GET /excusas/rechazadas (Excusas Rechazadas)
    // ====================================================================
    @Test
    void getRejectedExcuses_shouldReturn200_andRejectedExcuses() throws Exception {
        List<Excuse> rejectedList = Collections.singletonList(rejectedExcuse);
        when(excuseService.getRejectedExcuses()).thenReturn(rejectedList);

        mockMvc.perform(get("/excusas/rechazadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is(ExcuseStatus.Denied.toString())));

        verify(excuseService, times(1)).getRejectedExcuses();
    }

    // ====================================================================
    // 4. DELETE /excusas/eliminar?limiteDate={fecha} (Eliminación Segura)
    // ====================================================================
    @Test
    void deleteExcusesBeforeDate_shouldReturn200_andDeletionCount() throws Exception {
        LocalDate limiteDate = LocalDate.now().minusDays(7);
        int deletedCount = 5;

        when(excuseService.deleteExcusesBeforeDate(limiteDate)).thenReturn(deletedCount);

        mockMvc.perform(delete("/excusas/eliminar")
                        .param("limiteDate", limiteDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("They were successfully removed " + deletedCount + " excuses prior to the date " + limiteDate+".")));

        verify(excuseService, times(1)).deleteExcusesBeforeDate(limiteDate);
    }

    @Test
    void deleteExcusesBeforeDate_shouldReturn400_whenLimiteDateIsMissing() throws Exception {
        // Parámetro limiteDate ausente, lo cual debe disparar el 400 del controlador
        mockMvc.perform(delete("/excusas/eliminar"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("The 'limiteDate' parameter is mandatory for the bulk deletion operation.")));

        verify(excuseService, never()).deleteExcusesBeforeDate(any(LocalDate.class));
    }

    // ====================================================================
    // 5. GET /excusas (Filtros)
    // ====================================================================
    @Test
    void getAllExcusesWithFilters_shouldReturn200_andFilteredList() throws Exception {
        LocalDate dateFrom = LocalDate.now().minusDays(10);
        String status = ExcuseStatus.Accepted.toString();
        List<Excuse> filteredList = Collections.singletonList(approvedExcuse);

        when(excuseService.getAllExcusesWithFilters(
                eq(dateFrom),
                eq(null), // dateUntil null
                eq(status)
        )).thenReturn(filteredList);

        mockMvc.perform(get("/excusas")
                        .param("dateFrom", dateFrom.toString())
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is(status)));

        verify(excuseService, times(1)).getAllExcusesWithFilters(
                eq(dateFrom),
                eq(null),
                eq(status)
        );
    }
}