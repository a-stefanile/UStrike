package com.ustrike.control;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.service.PrenotazioneService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AnnullaPrenotazioneServletUnitTest {

    private AnnullaPrenotazioneServlet servlet;

    @Mock private PrenotazioneService serviceMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new AnnullaPrenotazioneServlet(serviceMock);
        
        when(request.getContextPath()).thenReturn("/UStrike");
    }

    // --- GOOD PATH ---

    @Test
    void testAnnulla_Successo() throws Exception {
        // Arrange
        setupSessioneValida(10); // userId = 10
        when(request.getParameter("idPrenotazione")).thenReturn("500");
        when(request.getMethod()).thenReturn("POST");

        // Act
        servlet.service(request, response);

        // Assert
        // Verifica che il service sia stato chiamato con i parametri corretti
        verify(serviceMock).annullaPrenotazioneCliente(eq(500), eq(10), eq(session));
        
        // Verifica il flash message e il redirect (Pattern Post-Redirect-Get)
        verify(session).setAttribute("flashMsg", "Prenotazione annullata con successo.");
        verify(response).sendRedirect("/UStrike/cliente/prenotazioni");
    }

    // --- BAD PATHS ---

    @Test
    void testAnnulla_UtenteNonLoggato_Unauthorized() throws Exception {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(null); // Nessuna sessione

        // Act
        servlet.service(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(serviceMock);
    }

    @Test
    void testAnnulla_RuoloSbagliato_Unauthorized() throws Exception {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("staff"); // Ruolo non autorizzato

        // Act
        servlet.service(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testAnnulla_ParametroMancante_BadRequest() throws Exception {
        // Arrange
        setupSessioneValida(10);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("idPrenotazione")).thenReturn(""); // Stringa vuota o null

        // Act
        servlet.service(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verifyNoInteractions(serviceMock);
    }

    // --- HELPER ---
    private void setupSessioneValida(int userId) {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("cliente");
        when(session.getAttribute("userId")).thenReturn(userId);
    }
}