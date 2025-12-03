package davinci.edu.ar.excusasSA.service;

import davinci.edu.ar.excusasSA.event.ProntuarioCreatedEvent;
import davinci.edu.ar.excusasSA.model.prontuario.Prontuario;
import davinci.edu.ar.excusasSA.repository.ProntuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProntuarioServiceTest {

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProntuarioService prontuarioService;

    private Prontuario mockProntuario;
    private final Long TEST_ID = 1L;

    @BeforeEach
    void setUp() {
        // Inicializar un mock de Prontuario genérico para usar en las respuestas
        mockProntuario = mock(Prontuario.class);
    }

    // ====================================================================
    // 1. getAllProntuarios()
    // ====================================================================

    @Test
    void getAllProntuarios_ShouldReturnListOfProntuarios() {
        // Arrange
        Prontuario prontuario2 = mock(Prontuario.class);
        List<Prontuario> expectedList = Arrays.asList(mockProntuario, prontuario2);
        when(prontuarioRepository.findAll()).thenReturn(expectedList);

        // Act
        List<Prontuario> result = prontuarioService.getAllProntuarios();

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedList, result);
        verify(prontuarioRepository, times(1)).findAll();
    }

    // ====================================================================
    // 2. getProntuarioById(Long id)
    // ====================================================================

    @Test
    void getProntuarioById_Success_ShouldReturnProntuario() {
        // Arrange
        when(prontuarioRepository.findById(TEST_ID)).thenReturn(Optional.of(mockProntuario));

        // Act
        Prontuario result = prontuarioService.getProntuarioById(TEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(mockProntuario, result);
        verify(prontuarioRepository, times(1)).findById(TEST_ID);
    }

    @Test
    void getProntuarioById_NotFound_ShouldThrowNoSuchElementException() {
        // Arrange
        when(prontuarioRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            prontuarioService.getProntuarioById(TEST_ID);
        });

        assertTrue(exception.getMessage().contains("Prontuario no encontrado con ID: " + TEST_ID));
        verify(prontuarioRepository, times(1)).findById(TEST_ID);
    }

    // ====================================================================
    // 3. addProntuario(Prontuario prontuario)
    // ====================================================================

    @Test
    void addProntuario_ShouldSaveProntuarioAndPublishEvent() {
        // Arrange
        Prontuario prontuarioToSave = new Prontuario();

        when(prontuarioRepository.save(prontuarioToSave)).thenReturn(mockProntuario);

        ArgumentCaptor<ProntuarioCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProntuarioCreatedEvent.class);

        // Act
        Prontuario result = prontuarioService.addProntuario(prontuarioToSave);

        // Assert
        // 1. Verifica que el repositorio fue llamado para guardar
        verify(prontuarioRepository, times(1)).save(prontuarioToSave);
        // 2. Verifica que devuelve el objeto guardado
        assertEquals(mockProntuario, result);

        // 3. Verifica que se publica el evento
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        // 4. Verifica el contenido del evento publicado
        ProntuarioCreatedEvent publishedEvent = eventCaptor.getValue();
        // Nota: Si mockProntuario.getId() no estuviera mockeado, el test podría fallar si se usa ese ID.
        assertEquals(mockProntuario, publishedEvent.getProntuario());
        assertEquals(prontuarioService, publishedEvent.getSource());
    }

    // ====================================================================
    // 4. resetProntuarios()
    // ====================================================================

    @Test
    void resetProntuarios_ShouldCallDeleteAllOnRepository() {
        // Act
        prontuarioService.resetProntuarios();

        // Assert
        // Verifica que se llamó al método deleteAll del repositorio
        verify(prontuarioRepository, times(1)).deleteAll();
    }
}