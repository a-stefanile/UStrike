package com.ustrike.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;

import jakarta.servlet.http.HttpSession;

class PrenotazioneServiceUnitTest {

    @Mock
    private PrenotazioneDAO daoMock;

    @Mock
    private HttpSession sessionMock;

    @InjectMocks
    private PrenotazioneService service;

    @BeforeEach
    void setUp() {
        
        daoMock = mock(PrenotazioneDAO.class);
        sessionMock = mock(HttpSession.class);
        service = new PrenotazioneService(daoMock);
    }

    @Test
    void testGetCatalogoInAttesa_Successo() throws Exception {
        List<Prenotazione> listaFinta = new ArrayList<>();
        when(daoMock.selectPrenotazioniInAttesa()).thenReturn(listaFinta);

        List<Prenotazione> risultato = service.getCatalogoInAttesa();

        assertNotNull(risultato);
        verify(daoMock).selectPrenotazioniInAttesa();
    }

    @Test
    void testGetPrenotazioniCliente_UsaCacheInSessione() {
        int idCliente = 1;
        String cacheKey = "prenotazioni_1";
        List<Prenotazione> listaInCache = new ArrayList<>();
        listaInCache.add(new Prenotazione());

        when(sessionMock.getAttribute(cacheKey)).thenReturn(listaInCache);

        List<Prenotazione> risultato = service.getPrenotazioniCliente(idCliente, sessionMock);

        assertSame(listaInCache, risultato, "Dovrebbe restituire l'oggetto dalla cache");
        verifyNoInteractions(daoMock);
    }

    
    @Test
    void testCreaPrenotazione_PulisceSessione() throws Exception { 
        // Arrange
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        int idCliente = 5;
        

        when(daoMock.insertPrenotazione(any(), any(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), any()))
            .thenReturn(100);


        int idGenerato = service.creaPrenotazione(ora, ora, "3 persone", 1, 1, idCliente, sessionMock);


        assertEquals(100, idGenerato);
        verify(sessionMock).removeAttribute("prenotazioni_5");
    }

    @Test
    void testAccettaPrenotazione_SuccessoEPuliziaCache() throws Exception { // <--- AGGIUNGI QUESTO

        int idPreno = 10;
        int idStaff = 2;
        int idCliente = 8;
        Prenotazione pFinta = new Prenotazione();
        pFinta.setIDCliente(idCliente);


        when(daoMock.updateStatoPrenotazione(idPreno, "Confermata", idStaff)).thenReturn(true);
        when(daoMock.selectPrenotazione(idPreno)).thenReturn(pFinta);

        // Act
        boolean esito = service.accettaPrenotazione(idPreno, idStaff, sessionMock);

        // Assert
        assertTrue(esito);
        verify(sessionMock).removeAttribute("prenotazioni_" + idCliente);
        verify(sessionMock).removeAttribute("prenotazioni_view_" + idCliente);
    }

    @Test
    void testGetCatalogoInAttesa_LanciaEccezione() throws Exception { 

        when(daoMock.selectPrenotazioniInAttesa()).thenThrow(new RuntimeException("DB Error"));


        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.getCatalogoInAttesa();
        });

        assertEquals("Errore catalogo in attesa", ex.getMessage());
    }
    @Test
    void testRifiutaPrenotazione_SuccessoEPuliziaCache() throws Exception {
        int idPreno = 20;
        int idStaff = 3;
        int idCliente = 15;
        Prenotazione pFinta = new Prenotazione();
        pFinta.setIDCliente(idCliente);

        when(daoMock.updateStatoPrenotazione(idPreno, "Rifiutata", idStaff)).thenReturn(true);
        when(daoMock.selectPrenotazione(idPreno)).thenReturn(pFinta);


        boolean esito = service.rifiutaPrenotazione(idPreno, idStaff, sessionMock);


        assertTrue(esito);
        verify(sessionMock).removeAttribute("prenotazioni_" + idCliente);
        verify(sessionMock).removeAttribute("prenotazioni_view_" + idCliente);
        System.out.println("Test Rifiuto: OK!");
    }
}