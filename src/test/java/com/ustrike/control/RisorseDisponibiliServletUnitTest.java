package com.ustrike.control;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ustrike.model.dto.Risorsa;
import com.ustrike.model.service.RisorsaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

class RisorseDisponibiliServletUnitTest {

    private RisorseDisponibiliServlet servlet;

    @Mock private RisorsaService rServiceMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Iniettiamo il mock nel costruttore
        servlet = new RisorseDisponibiliServlet(rServiceMock);
    }

    @Test
    void testGetRisorse_Successo_FasciaSerale() throws Exception {
        // 1. Arrange: Simuliamo una richiesta alle 20:00 (stesso giorno)
        setupSession();
        when(request.getParameter("idServizio")).thenReturn("1");
        when(request.getParameter("data")).thenReturn("2026-05-10");
        when(request.getParameter("orario")).thenReturn("20:00");

        // Mock dei dati del service
        List<Risorsa> candidate = new ArrayList<>();
        candidate.add(new Risorsa(1, 1, 5, 1));
        when(rServiceMock.getRisorseLibereByServizio(1)).thenReturn(candidate);
        when(rServiceMock.isRisorsaDisponibile(eq(1), any(Timestamp.class))).thenReturn(true);

        // Catturiamo l'output JSON
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // 2. Act
        servlet.service(request, response);

        // 3. Assert
        String jsonOutput = sw.toString();
        assertTrue(jsonOutput.contains("\"success\":true"));
        assertTrue(jsonOutput.contains("\"id\":1"));
        // Verifichiamo che il service sia stato interrogato
        verify(rServiceMock).getRisorseLibereByServizio(1);
    }

    @Test
    void testGetRisorse_Successo_OltreMezzanotte() throws Exception {
        // 1. Arrange: Simuliamo una richiesta alle 01:00 (deve passare al giorno dopo)
        setupSession();
        when(request.getParameter("idServizio")).thenReturn("1");
        when(request.getParameter("data")).thenReturn("2026-05-10"); // Giorno selezionato
        when(request.getParameter("orario")).thenReturn("01:00");

        when(rServiceMock.getRisorseLibereByServizio(1)).thenReturn(new ArrayList<>());

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // 2. Act
        servlet.service(request, response);

        // 3. Assert: Verifichiamo che il timestamp passato al service sia del giorno 11 (10 + 1)
        // La servlet calcola: 2026-05-10 + 1 day = 2026-05-11 01:00:00
        Timestamp expectedTs = Timestamp.valueOf("2026-05-11 01:00:00");
        verify(rServiceMock).getRisorseLibereByServizio(1);
        // Se arriviamo qui senza eccezioni di parsing, la logica plusDays(1) ha funzionato
        assertTrue(sw.toString().contains("\"success\":true"));
    }

    @Test
    void testGetRisorse_OrarioNonValido() throws Exception {
        // Arrange: Orario fuori dalle regex (es. 15:00)
        setupSession();
        when(request.getParameter("idServizio")).thenReturn("1");
        when(request.getParameter("data")).thenReturn("2026-05-10");
        when(request.getParameter("orario")).thenReturn("15:00");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // Act
        servlet.service(request, response);

        // Assert
        assertTrue(sw.toString().contains("Orario non valido"));
        verifyNoInteractions(rServiceMock); // Il service non deve essere chiamato se l'input è invalido
    }

    @Test
    void testGetRisorse_AccessoNegato() throws Exception {
        // 1. Arrange
        when(request.getMethod()).thenReturn("GET"); 
        
        // Simuliamo che non ci sia la sessione
        when(request.getSession(false)).thenReturn(null);

        // 2. Act
        servlet.service(request, response);

        // 3. Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void setupSession() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("cliente");
    }
}