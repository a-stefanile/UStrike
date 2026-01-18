package com.ustrike.control;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ustrike.model.CatalogoPrenotazioni;
import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
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

class StaffDashboardServletUnitTest {

    private StaffDashboardServlet servlet;

    @Mock private CatalogoPrenotazioni catalogoMock;
    @Mock private PrenotazioneDAO daoMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new StaffDashboardServlet(catalogoMock, daoMock);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet_DefaultFiltroInAttesa() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("filter")).thenReturn(null);
        
        List<Prenotazione> fintaLista = new ArrayList<>();
        when(catalogoMock.getPrenotazioniInAttesa()).thenReturn(fintaLista);

        servlet.service(request, response);

        verify(request).setAttribute("prenotazioni", fintaLista);
        // Nota: la tua servlet usa "In attesa" con la I maiuscola
        verify(request).setAttribute("filter", "In attesa");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_AccettaSuccesso() throws Exception {
        setupStaffSession();
        when(request.getParameter("action")).thenReturn("accetta");
        when(request.getParameter("idPrenotazione")).thenReturn("10");
        
        
        when(daoMock.updateStatoPrenotazione(10, "Confermata", 1, null)).thenReturn(true);
        
        // Simuliamo anche la selectPrenotazione successiva (riga 131 della Servlet)
        when(daoMock.selectPrenotazione(10)).thenReturn(new Prenotazione());

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.service(request, response);

        assertTrue(sw.toString().contains("{\"success\":true}"));
        // CORREZIONE: Verifica con 4 parametri
        verify(daoMock).updateStatoPrenotazione(10, "Confermata", 1, null);
    }

    @Test
    void testDoPost_RifiutaSuccesso() throws Exception {
        setupStaffSession();
        when(request.getParameter("action")).thenReturn("rifiuta");
        when(request.getParameter("idPrenotazione")).thenReturn("10");
        when(request.getParameter("motivo")).thenReturn("Pista in manutenzione");
        
        // CORREZIONE: Aggiunto il motivo come quarto parametro come da Servlet riga 124
        when(daoMock.updateStatoPrenotazione(10, "Rifiutata", 1, "Pista in manutenzione")).thenReturn(true);
        when(daoMock.selectPrenotazione(10)).thenReturn(new Prenotazione());

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.service(request, response);

        assertTrue(sw.toString().contains("{\"success\":true}"));
        verify(daoMock).updateStatoPrenotazione(10, "Rifiutata", 1, "Pista in manutenzione");
    }

    @Test
    void testDoPost_RifiutaSenzaMotivo() throws Exception {
        setupStaffSession();
        when(request.getParameter("action")).thenReturn("rifiuta");
        when(request.getParameter("idPrenotazione")).thenReturn("10");
        when(request.getParameter("motivo")).thenReturn(null); // Motivo mancante

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.service(request, response);

        assertTrue(sw.toString().contains("Motivo rifiuto mancante"));
    }

    private void setupStaffSession() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("ruolo")).thenReturn("staff");
        when(session.getAttribute("userId")).thenReturn(1);
    }
}