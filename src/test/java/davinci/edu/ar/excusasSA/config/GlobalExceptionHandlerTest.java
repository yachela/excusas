package davinci.edu.ar.excusasSA.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Necesario si no usas WebMvcTest
import org.springframework.boot.test.context.SpringBootTest; // Usamos SpringBootTest
import org.springframework.test.context.ContextConfiguration; // Usamos ContextConfiguration
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Usar @SpringBootTest en modo MOCK para un contexto más completo.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
// 2. Usar @ContextConfiguration para forzar la inclusión de las clases.
// Esto asegura que Spring sepa exactamente qué Beans debe registrar como Controllers y Advices.
@ContextConfiguration(classes = {
        GlobalExceptionHandlerTest.TestController.class,
        GlobalExceptionHandler.class
})
@AutoConfigureMockMvc // Asegura que MockMvc esté configurado.
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // 1. Controller de Prueba (se mantiene igual, debe ser public static)
    @RestController
    public static class TestController {

        @GetMapping("/test/404")
        public void throwNotFound() {
            throw new NoSuchElementException("Item no encontrado.");
        }

        @GetMapping("/test/400")
        public void throwBadRequest() {
            throw new IllegalArgumentException("Dato inválido.");
        }

        @GetMapping("/test/500")
        public void throwGeneralError() {
            throw new RuntimeException("Error inesperado de DB.");
        }
    }

    // ====================================================================
    // Tests de Mapeo de Excepciones (CORREGIDOS)
    // ====================================================================

    @Test
    void handleNotFoundException_ShouldReturn404AndMessage() throws Exception {
        mockMvc.perform(get("/test/404"))
                .andExpect(status().isNotFound())
                // CORREGIDO: Esperamos el prefijo de la excepción + el mensaje del controller
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Recurso no encontrado. Detalle: Item no encontrado.")));
    }

    @Test
    void handleBadRequestException_ShouldReturn400AndMessage() throws Exception {
        mockMvc.perform(get("/test/400"))
                .andExpect(status().isBadRequest())
                // CORREGIDO: Esperamos el prefijo de la excepción + el mensaje del controller
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error en la lógica de negocio. Detalle: Dato inválido.")));
    }

    @Test
    void handleGeneralException_ShouldReturn500AndGenericMessage() throws Exception {
        mockMvc.perform(get("/test/500"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor. Contacte al administrador."));
    }
}