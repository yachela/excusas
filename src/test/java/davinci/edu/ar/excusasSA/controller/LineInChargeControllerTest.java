package davinci.edu.ar.excusasSA.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import davinci.edu.ar.excusasSA.model.lineincharge.ChainLine;
import davinci.edu.ar.excusasSA.model.lineincharge.LineInCharge;
import davinci.edu.ar.excusasSA.model.employee.incharge.Receptionist;
import davinci.edu.ar.excusasSA.service.LineInChargeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // NUEVO IMPORT
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

    // Usamos el ObjectMapper inyectado por Spring Boot, que ahora sabe cómo serializar
    // la herencia gracias a las anotaciones en Employee.java.
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LineInChargeService lineInChargeService;

    private LineInCharge mockSavedConfig;
    private LineInCharge mockInputConfig;
    private final String CHAIN_CODE = "HR_DEPT";
    private final Long LEGAJO = 600L;
    private final String STRATEGY_PRODUCTIVE = "PRODUCTIVE";
    private final String STRATEGY_LAZY = "LAZY";

    private final Long MOCK_ID_FOR_INPUT = 1L;

    @BeforeEach
    void setUp() {
        // --- Objetos para la Respuesta Mockeada (Lo que el Servicio *devuelve*) ---
        ChainLine mockChainLineResponse = new ChainLine(MOCK_ID_FOR_INPUT, CHAIN_CODE);

        // La clase concreta a usar es Receptionist
        Receptionist mockReceptionistResponse = new Receptionist(
                "Receptionist Test Name",
                "receptionist@sa.com",
                LEGAJO,
                null
        );
        mockReceptionistResponse.setId(MOCK_ID_FOR_INPUT);

        mockSavedConfig = new LineInCharge(
                1L, // ID de la LineInCharge (PK)
                mockChainLineResponse,
                mockReceptionistResponse,
                2,
                STRATEGY_PRODUCTIVE
        );

        // --- Objeto para la Solicitud Mockeada (Lo que el Controlador *recibe*) ---
        ChainLine inputChainLine = new ChainLine(MOCK_ID_FOR_INPUT, CHAIN_CODE);

        // La clase concreta a serializar es Receptionist
        Receptionist inputReceptionist = new Receptionist(
                "Receptionist Test Name",
                "receptionist@sa.com",
                LEGAJO,
                null
        );
        inputReceptionist.setId(MOCK_ID_FOR_INPUT);

        mockInputConfig = new LineInCharge(
                null, // El ID de la LineInCharge debe ser null al crear
                inputChainLine,
                inputReceptionist,
                2,
                STRATEGY_PRODUCTIVE
        );
    }

    /**
     * Test para POST /encargados
     * Registra una nueva configuración de línea. El ObjectMapper ahora serializa
     * correctamente el tipo de empleado (incluyendo la propiedad @class) gracias
     * a las anotaciones en la clase Employee.
     */
    @Test
    void addLineConfig_shouldReturn200_andSavedConfig() throws Exception {
        // 1. Mockeamos el servicio: cuando se llame a createLineConfig, devolvemos mockSavedConfig.
        when(lineInChargeService.createLineConfig(any(LineInCharge.class))).thenReturn(mockSavedConfig);

        // 2. Generamos el cuerpo del JSON a partir del objeto Java.
        // El ObjectMapper inyectado automáticamente incluye el campo "@class" en el JSON.
        String requestBody = objectMapper.writeValueAsString(mockInputConfig);

        // 3. Ejecutamos la petición y verificamos el resultado.
        mockMvc.perform(post("/encargados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.employee.legajo").value(LEGAJO));
    }

    /**
     * Test para PUT /encargados/modo
     * Actualiza el modo de estrategia de un encargado usando RequestParams.
     */
    @Test
    void updateMode_shouldReturn200_andUpdatedConfig() throws Exception {
        // Creamos una configuración mockeada que simula el resultado actualizado.
        LineInCharge mockUpdatedConfig = new LineInCharge(
                mockSavedConfig.getId(),
                mockSavedConfig.getChainLine(),
                mockSavedConfig.getEmployee(),
                mockSavedConfig.getOrderIndex(),
                STRATEGY_LAZY // Cambiamos el modo a "LAZY"
        );

        // 1. Mockeamos el servicio:
        when(lineInChargeService.updateStrategyMode(
                eq(LEGAJO),
                eq(CHAIN_CODE),
                eq(STRATEGY_LAZY)
        )).thenReturn(mockUpdatedConfig);

        // 2. Ejecutamos la petición con RequestParams
        mockMvc.perform(put("/encargados/modo")
                        .param("legajo", String.valueOf(LEGAJO))
                        .param("chainIdCode", CHAIN_CODE)
                        .param("newMode", STRATEGY_LAZY))

                // 3. Verificamos el resultado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strategyMode").value(STRATEGY_LAZY));
    }

    /**
     * Test para GET /encargados
     * Obtiene la lista completa de configuraciones de línea.
     */
    @Test
    void getAllLineInCharge_shouldReturn200_andListOfConfigs() throws Exception {
        List<LineInCharge> allConfigs = Collections.singletonList(mockSavedConfig);

        // 1. Mockeamos el servicio:
        when(lineInChargeService.getAllLineInCharge()).thenReturn(allConfigs);

        // 2. Ejecutamos la petición GET
        mockMvc.perform(get("/encargados")
                        .contentType(MediaType.APPLICATION_JSON))

                // 3. Verificamos el resultado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].employee.legajo").value(LEGAJO));
    }
}