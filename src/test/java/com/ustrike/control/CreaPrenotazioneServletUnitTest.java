package com.ustrike.control;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ustrike.model.service.PrenotazioneService;
import com.ustrike.model.service.RisorsaService;
import com.ustrike.model.service.ServizioService;
import com.ustrike.model.dto.Servizio;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

class CreaPrenotazioneServletUnitTest {

    private CreaPrenotazioneServlet servlet;

    @Mock private PrenotazioneService pService;
    @Mock private ServizioService sService;
    @Mock private RisorsaService rService;
    
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Iniettiamo i mock tramite il costruttore creato apposta
        servlet = new CreaPrenotazioneServlet(pService, sService, rService);
        
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet_CaricamentoForm() throws Exception {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("tipo")).thenReturn("bowling");
        
        List<Servizio> fintiServizi = new ArrayList<>();
        fintiServizi.add(new Servizio(10, "Bowling", true));
        when(sService.getServiziAbilitati()).thenReturn(fintiServizi);

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("idServizioSelezionato"), eq(10));
        verify(request).setAttribute(eq("tipo"), eq("bowling"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_Successo() throws Exception {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("cliente");
        when(session.getAttribute("userId")).thenReturn(1);

        // Parametri del form
        when(request.getParameter("data")).thenReturn("2026-05-20");
        when(request.getParameter("orario")).thenReturn("21:00");
        when(request.getParameter("idServizio")).thenReturn("10");
        when(request.getParameter("idRisorsa")).thenReturn("5");
        when(request.getParameter("numPartecipanti")).thenReturn("1");
        when(request.getParameter("partecipante1")).thenReturn("Mario Rossi");

        // Mock dei service
        when(rService.isRisorsaDisponibile(anyInt(), any())).thenReturn(true);
        when(pService.creaPrenotazione(any(), any(), anyString(), anyInt(), anyInt(), anyInt(), any())).thenReturn(100);

        // Catturiamo il JSON di risposta
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        servlet.service(request, response);

        // Assert
        writer.flush();
        assertTrue(stringWriter.toString().contains("\"success\":true"));
        assertTrue(stringWriter.toString().contains("Richiesta inviata con successo!"));
    }

    @Test
    void testDoPost_RisorsaOccupata() throws Exception {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("cliente");
        when(session.getAttribute("userId")).thenReturn(1);
        
        when(request.getParameter("data")).thenReturn("2026-05-20");
        when(request.getParameter("orario")).thenReturn("21:00");
        when(request.getParameter("idServizio")).thenReturn("10");
        when(request.getParameter("idRisorsa")).thenReturn("5");
        when(request.getParameter("numPartecipanti")).thenReturn("1");

        // Mock: risorsa NON disponibile
        when(rService.isRisorsaDisponibile(anyInt(), any())).thenReturn(false);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        servlet.service(request, response);

        // Assert
        writer.flush();
        assertTrue(stringWriter.toString().contains("\"success\":false"));
    }
}