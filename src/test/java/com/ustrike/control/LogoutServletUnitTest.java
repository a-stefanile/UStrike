package com.ustrike.control;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class LogoutServletUnitTest {

    private LogoutServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new LogoutServlet();
        
        // Setup del context path per il redirect
        when(request.getContextPath()).thenReturn("/UStrike");
    }

    @Test
    void testLogout_SessioneEsistente() throws ServletException, IOException {
        // 1. Arrange: Simuliamo una sessione attiva con dei dati
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        
        // Mock dei dati per il log di audit ( System.out.printf )
        when(session.getAttribute("ruolo")).thenReturn("cliente");
        when(session.getAttribute("nomeUtente")).thenReturn("Mario");
        when(session.getAttribute("userId")).thenReturn(1);

        // 2. Act
        servlet.service(request, response);

        // 3. Assert: La sessione deve essere invalidata
        verify(session, times(1)).invalidate();
        // L'utente deve essere rediretto a /UStrike/login
        verify(response).sendRedirect("/UStrike/login");
    }

    @Test
    void testLogout_SessioneNulla() throws ServletException, IOException {
        // 1. Arrange: Simuliamo che la sessione sia già scaduta o nulla
        when(request.getMethod()).thenReturn("POST"); // Testiamo anche il POST
        when(request.getSession(false)).thenReturn(null);

        // 2. Act
        servlet.service(request, response);

        // 3. Assert: Non deve esplodere e deve comunque fare il redirect
        verify(response).sendRedirect("/UStrike/login");
        // Verifichiamo che non ci siano interazioni con l'oggetto sessione (visto che è null)
        verifyNoInteractions(session);
    }
}