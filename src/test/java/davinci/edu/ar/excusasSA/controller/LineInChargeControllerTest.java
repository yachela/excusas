package davinci.edu.ar.excusasSA.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import davinci.edu.ar.excusasSA.dto.LineInChargeDTO;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.service.LineInChargeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LineInChargeController.class)
@ActiveProfiles("test")
class LineInChargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineInChargeService lineInChargeService;

    // --- Variables de Mockeo Comunes ---
    private final String CHAIN_CODE = "HR_DEPT";
    private final Long LEGAJO = 600L;
    private final String STRATEGY_PRODUCTIVE = "PRODUCTIVE";
    private final String STRATEGY_LAZY = "LAZY";
    private final Long MOCK_ID = 1L;

    // --- DTO para la Petición POST ---
    private LineInChargeDTO mockInputDTO;

    // --- Entidad para la Respuesta Mockeada del Servicio (Entidad interna) ---
    private LineInCharge mockSavedEntity;

    // --- DTO para la Respuesta Esperada del Controlador (DTO externo) ---
    private LineInChargeDTO mockResponseDTO;


    @BeforeEach
    void setUp() {
        // 1. DTO de ENTRADA (Lo que el Controller recibe del JSON)
        mockInputDTO = new LineInChargeDTO(
                null,
                LEGAJO,
                CHAIN_CODE,
                2,
                STRATEGY_PRODUCTIVE
        );

        // 2. ENTIDAD Guardada (Lo que el Service devuelve al Controller)
        ChainLine mockChainLine = new ChainLine(MOCK_ID, CHAIN_CODE);
        Receptionist mockEmployee = new Receptionist("Dev Test Name", "dev@sa.com", LEGAJO, null);
        mockEmployee.setId(MOCK_ID); // Necesario para la FK si se serializara

        mockSavedEntity = new LineInCharge(
                MOCK_ID, // ID de la LineInCharge
                mockChainLine,
                mockEmployee,
                2,
                STRATEGY_PRODUCTIVE
        );

        // 3. DTO de SALIDA (Lo que el Controller mapea y devuelve al cliente)
        mockResponseDTO = new LineInChargeDTO(
                MOCK_ID,
                LEGAJO,
                CHAIN_CODE,
                2,
                STRATEGY_PRODUCTIVE
        );
    }

    /**
     * Test para POST /encargados
     * Registra una nueva configuración de línea. Ahora mockeamos el servicio
     * y verificamos que el JSON de respuesta es un DTO.
     */
    @Test
    void addLineConfig_shouldReturn200_andSavedDTO() throws Exception {
        // 1. Mockeamos el servicio: Esperamos un DTO en la entrada (any(LineInChargeDTO.class))
        // El servicio internamente hace el mapeo, pero el Controller solo ve el DTO.
        // **IMPORTANTE**: El servicio devuelve una Entidad, no un DTO.
        when(lineInChargeService.createLineConfig(any(LineInChargeDTO.class))).thenReturn(mockSavedEntity);

        // 2. Generamos el cuerpo del JSON a partir del DTO de entrada (mucho más simple)
        String requestBody = objectMapper.writeValueAsString(mockInputDTO);

        // 3. Ejecutamos la petición y verificamos el resultado.
        mockMvc.perform(post("/encargados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(status().isOk())
                // Verificamos los campos del DTO de SALIDA
                .andExpect(jsonPath("$.id").value(MOCK_ID))
                .andExpect(jsonPath("$.employeeLegajo").value(LEGAJO))
                .andExpect(jsonPath("$.chainIdCode").value(CHAIN_CODE))
                .andExpect(jsonPath("$.orderIndex").value(2));
    }

    /**
     * Test para PUT /encargados/modo
     * Actualiza el modo de estrategia de un encargado usando RequestParams.
     * Este endpoint aún devuelve la Entidad LineInCharge (como definiste).
     */
    @Test
    void updateMode_shouldReturn200_andUpdatedConfig() throws Exception {
        // Creamos una configuración mockeada que simula el resultado actualizado.
        LineInCharge mockUpdatedEntity = new LineInCharge(
                mockSavedEntity.getId(),
                mockSavedEntity.getChainLine(),
                mockSavedEntity.getEmployee(),
                mockSavedEntity.getOrderIndex(),
                STRATEGY_LAZY // Cambiamos el modo a "LAZY"
        );

        // 1. Mockeamos el servicio:
        when(lineInChargeService.updateStrategyMode(
                eq(LEGAJO),
                eq(CHAIN_CODE),
                eq(STRATEGY_LAZY)
        )).thenReturn(mockUpdatedEntity);

        // 2. Ejecutamos la petición con RequestParams
        mockMvc.perform(put("/encargados/modo")
                        .param("legajo", String.valueOf(LEGAJO))
                        .param("chainIdCode", CHAIN_CODE)
                        .param("newMode", STRATEGY_LAZY))

                // 3. Verificamos el resultado (que es la Entidad)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strategyMode").value(STRATEGY_LAZY));
    }

    /**
     * Test para GET /encargados
     * Obtiene la lista completa de configuraciones de línea (Entidades).
     */
    @Test
    void getAllLineInCharge_shouldReturn200_andListOfConfigs() throws Exception {
        List<LineInCharge> allConfigs = Collections.singletonList(mockSavedEntity);

        // 1. Mockeamos el servicio:
        when(lineInChargeService.getAllLineInCharge()).thenReturn(allConfigs);

        // 2. Ejecutamos la petición GET
        mockMvc.perform(get("/encargados")
                        .contentType(MediaType.APPLICATION_JSON))

                // 3. Verificamos el resultado (que es la Entidad)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                // Verificamos la estructura anidada de la Entidad
                .andExpect(jsonPath("$[0].employee.legajo").value(LEGAJO));
    }
}