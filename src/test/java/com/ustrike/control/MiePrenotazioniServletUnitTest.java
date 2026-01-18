package com.ustrike.control;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.service.PrenotazioneService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class MiePrenotazioniServletUnitTest {

    private MiePrenotazioniServlet servlet;

    @Mock private PrenotazioneService pServiceMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Iniettiamo il mock nel costruttore della Servlet
        servlet = new MiePrenotazioniServlet(pServiceMock);
        
        // Setup del dispatcher per evitare NullPointerException durante il forward
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet_UtenteLoggato_MostraPrenotazioni() throws ServletException, IOException {
        // 1. Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("cliente");
        when(session.getAttribute("userId")).thenReturn(10);

        // Usiamo PrenotazioneView come richiesto dalla Servlet
        List<com.ustrike.model.dto.PrenotazioneView> fintaListaView = new ArrayList<>();
        fintaListaView.add(new com.ustrike.model.dto.PrenotazioneView()); 
        
        // Configuriamo il mock per il metodo View
        when(pServiceMock.getPrenotazioniClienteView(10, session)).thenReturn(fintaListaView);

        // 2. Act
        servlet.service(request, response);

        // 3. Assert
        // Verifichiamo che la Servlet abbia chiamato il metodo View
        verify(pServiceMock).getPrenotazioniClienteView(10, session);
        
        // Verifichiamo che l'attributo passato alla JSP sia quello corretto
        verify(request).setAttribute(eq("prenotazioni"), eq(fintaListaView));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_AccessoNegato_SessioneNulla() throws ServletException, IOException {
        // 1. Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(null);

        // 2. Act
        servlet.service(request, response);

        // 3. Assert
        // Verifichiamo che la servlet risponda con errore 401 o redirect (in base alla tua logica)
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoInteractions(pServiceMock);
    }

    @Test
    void testDoGet_AccessoNegato_RuoloSbagliato() throws ServletException, IOException {
        // 1. Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("staff"); // Lo staff non deve vedere le prenotazioni cliente qui

        // 2. Act
        servlet.service(request, response);

        // 3. Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}