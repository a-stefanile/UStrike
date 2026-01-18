package com.ustrike.control;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class BiliardoServletUnitTest {

    private BiliardoServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        // Inizializza i mock annotati
        MockitoAnnotations.openMocks(this);
        
        // Crea l'istanza della servlet da testare
        servlet = new BiliardoServlet();
        
        // Configurazioni comuni: il context path e il dispatcher della JSP
        when(request.getContextPath()).thenReturn("/UStrike");
        when(request.getRequestDispatcher("/view/jsp/biliardo.jsp")).thenReturn(dispatcher);
    }

    @Test
    void testNavigazioneUtenteAnonimo() throws ServletException, IOException {
        // 1. Arrange: Chiamata GET senza alcuna sessione
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(null);

        // 2. Act: Usiamo service() per attivare internamente doGet()
        servlet.service(request, response);

        // 3. Assert: Verifica che homeUrl sia quella generale
        verify(request).setAttribute("homeUrl", "/UStrike/view/jsp/homeGenerale.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testNavigazioneClienteLoggato() throws ServletException, IOException {
        // 1. Arrange: Chiamata GET con sessione cliente
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("cliente");

        // 2. Act
        servlet.service(request, response);

        // 3. Assert: Verifica che homeUrl punti alla home del cliente
        verify(request).setAttribute("homeUrl", "/UStrike/cliente/home");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testNavigazioneAltroRuolo() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("manager");
        servlet.service(request, response);

        
        verify(request).setAttribute("homeUrl", "/UStrike/view/jsp/homeGenerale.jsp");
        verify(dispatcher).forward(request, response);
    }
}