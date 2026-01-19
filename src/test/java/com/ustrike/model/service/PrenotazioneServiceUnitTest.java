package com.ustrike.model.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import com.ustrike.model.dto.PrenotazioneView;

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
        MockitoAnnotations.openMocks(this);
    }

    // --- TEST DI SUCCESSO E ALLINEAMENTO FIRME ---

    @Test
    void testCreaPrenotazione_Allineato() throws Exception {
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        int idCliente = 5;

        // Verifica che il service passi 9 parametri al DAO (inclusi null per staff e note)
        when(daoMock.insertPrenotazione(any(), any(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), any(), any()))
            .thenReturn(100);

        int idGenerato = service.creaPrenotazione(ora, ora, "3 persone", 1, 1, idCliente, sessionMock);

        assertEquals(100, idGenerato);
        verify(sessionMock).removeAttribute("prenotazioni_5");
        verify(sessionMock).removeAttribute("prenotazioni_view_5");
    }

    @Test
    void testAccettaPrenotazione_Allineato() throws Exception {
        int idPreno = 10;
        int idStaff = 2;
        int idCliente = 8;
        Prenotazione pFinta = new Prenotazione();
        pFinta.setIDCliente(idCliente);

        when(daoMock.updateStatoPrenotazione(eq(idPreno), eq("Confermata"), eq(idStaff), anyString())).thenReturn(true);
        when(daoMock.selectPrenotazione(idPreno)).thenReturn(pFinta);

        boolean esito = service.accettaPrenotazione(idPreno, idStaff, sessionMock);

        assertTrue(esito);
        verify(sessionMock).removeAttribute("prenotazioni_" + idCliente);
    }

    @Test
    void testRifiutaPrenotazione_ConMotivo() throws Exception {
        int idPreno = 20;
        int idStaff = 3;
        int idCliente = 15;
        String motivo = "Al completo";
        Prenotazione pFinta = new Prenotazione();
        pFinta.setIDCliente(idCliente);

        when(daoMock.updateStatoPrenotazione(idPreno, "Rifiutata", idStaff, motivo)).thenReturn(true);
        when(daoMock.selectPrenotazione(idPreno)).thenReturn(pFinta);

        boolean esito = service.rifiutaPrenotazione(idPreno, idStaff, motivo, sessionMock);

        assertTrue(esito);
        verify(daoMock).updateStatoPrenotazione(idPreno, "Rifiutata", idStaff, motivo);
        verify(sessionMock).removeAttribute("prenotazioni_" + idCliente);
    }

    // --- TEST LOGICA CACHE ---

    @Test
    void testGetPrenotazioniClienteView_CacheHit() throws Exception {
        int idCliente = 1;
        String cacheKey = "prenotazioni_view_1";
        List<PrenotazioneView> listaInCache = new ArrayList<>();

        when(sessionMock.getAttribute(cacheKey)).thenReturn(listaInCache);

        List<PrenotazioneView> risultato = service.getPrenotazioniClienteView(idCliente, sessionMock);

        assertSame(listaInCache, risultato);
        verifyNoInteractions(daoMock); // Non deve andare a DB
    }

    @Test
    void testGetPrenotazioniClienteView_CacheMiss() throws Exception {
        int idCliente = 1;
        String cacheKey = "prenotazioni_view_1";
        List<PrenotazioneView> listaDalloStore = new ArrayList<>();
        
        when(sessionMock.getAttribute(cacheKey)).thenReturn(null);
        when(daoMock.selectPrenotazioniByClienteView(idCliente)).thenReturn(listaDalloStore);

        List<PrenotazioneView> risultato = service.getPrenotazioniClienteView(idCliente, sessionMock);

        assertNotNull(risultato);
        verify(sessionMock).setAttribute(cacheKey, listaDalloStore);
    }

    // --- TEST ROBUSTEZZA ED ERRORI (EDGE CASES) ---

    @Test
    void testCreaPrenotazione_SessioneNull_NonCrasha() throws Exception {
        // Verifica che il service gestisca il caso in cui la sessione non esista (es. chiamate API)
        when(daoMock.insertPrenotazione(any(), any(), anyString(), anyString(), anyInt(), anyInt(), anyInt(), any(), any()))
            .thenReturn(100);

        assertDoesNotThrow(() -> {
            service.creaPrenotazione(new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), 
                                    "2 persone", 1, 1, 5, null);
        });
    }

    @Test
    void testAccettaPrenotazione_FallimentoDAO_NonPulisceCache() throws Exception {
        int idPreno = 10;
        // Se il DAO restituisce false (es. record già modificato da altri)
        when(daoMock.updateStatoPrenotazione(anyInt(), anyString(), anyInt(), anyString())).thenReturn(false);

        boolean esito = service.accettaPrenotazione(idPreno, 1, sessionMock);

        assertFalse(esito);
        // Non deve provare a recuperare il cliente né pulire la cache
        verify(daoMock, never()).selectPrenotazione(anyInt());
        verify(sessionMock, never()).removeAttribute(anyString()); 
    }

    @Test
    void testAccettaPrenotazione_ErroreRecuperoCliente_MaEsitoPositivo() throws Exception {
        int idPreno = 10;
        when(daoMock.updateStatoPrenotazione(anyInt(), anyString(), anyInt(), anyString())).thenReturn(true);
        // Simulo un errore nel recupero del cliente (usato solo per la cache)
        when(daoMock.selectPrenotazione(idPreno)).thenThrow(new RuntimeException("DB down"));

        boolean esito = service.accettaPrenotazione(idPreno, 1, sessionMock);

        // L'operazione principale deve essere considerata riuscita (try-catch ignored nel service)
        assertTrue(esito);
    }

    @Test
    void testGetCatalogoInAttesa_ErrorWrapping() throws Exception {
        when(daoMock.selectPrenotazioniInAttesa()).thenThrow(new RuntimeException("SQL Error"));

        Exception ex = assertThrows(RuntimeException.class, () -> service.getCatalogoInAttesa());
        assertEquals("Errore catalogo in attesa", ex.getMessage());
    }
 // --- TEST getCatalogoCompletate ---

    @Test
    void testGetCatalogoCompletate_ErrorWrapping() throws Exception {
        when(daoMock.selectPrenotazioniCompletate()).thenThrow(new RuntimeException("SQL Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getCatalogoCompletate());
        assertEquals("Errore catalogo completate", ex.getMessage());
        assertNotNull(ex.getCause());
    }

 // --- TEST getPrenotazioniCliente (cache + error wrapping) ---

    @Test
    void testGetPrenotazioniCliente_CacheHit() throws Exception {
        int idCliente = 1;
        String cacheKey = "prenotazioni_1";
        List<Prenotazione> listaInCache = new ArrayList<>();

        when(sessionMock.getAttribute(cacheKey)).thenReturn(listaInCache);

        List<Prenotazione> risultato = service.getPrenotazioniCliente(idCliente, sessionMock);

        assertSame(listaInCache, risultato);
        verifyNoInteractions(daoMock);
    }

    @Test
    void testGetPrenotazioniCliente_CacheMiss() throws Exception {
        int idCliente = 1;
        String cacheKey = "prenotazioni_1";
        List<Prenotazione> listaDalloStore = new ArrayList<>();

        when(sessionMock.getAttribute(cacheKey)).thenReturn(null);
        when(daoMock.selectPrenotazioniByCliente(idCliente)).thenReturn(listaDalloStore);

        List<Prenotazione> risultato = service.getPrenotazioniCliente(idCliente, sessionMock);

        assertSame(listaDalloStore, risultato);
        verify(sessionMock).setAttribute(cacheKey, listaDalloStore);
    }

    @Test
    void testGetPrenotazioniCliente_ErrorWrapping() throws Exception {
        int idCliente = 1;
        when(sessionMock.getAttribute("prenotazioni_1")).thenReturn(null);
        when(daoMock.selectPrenotazioniByCliente(idCliente)).thenThrow(new RuntimeException("SQL Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getPrenotazioniCliente(idCliente, sessionMock));

        assertEquals("Errore prenotazioni cliente", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void testGetPrenotazioniCliente_SessioneNull_Funziona() throws Exception {
        int idCliente = 1;
        List<Prenotazione> listaDalloStore = new ArrayList<>();
        when(daoMock.selectPrenotazioniByCliente(idCliente)).thenReturn(listaDalloStore);

        List<Prenotazione> risultato = service.getPrenotazioniCliente(idCliente, null);

        assertSame(listaDalloStore, risultato);
    }

 // TEST annullaPrenotazioneCliente ---

    @Test
    void testAnnullaPrenotazioneCliente_Successo_PulisceCache() throws Exception {
        int idPreno = 10;
        int idCliente = 5;

        when(daoMock.annullaPrenotazioneCliente(idPreno, idCliente)).thenReturn(true);

        boolean esito = service.annullaPrenotazioneCliente(idPreno, idCliente, sessionMock);

        assertTrue(esito);
        verify(sessionMock).removeAttribute("prenotazioni_" + idCliente);
        verify(sessionMock).removeAttribute("prenotazioni_view_" + idCliente);
    }

    @Test
    void testAnnullaPrenotazioneCliente_Fallimento_NonPulisceCache() throws Exception {
        int idPreno = 10;
        int idCliente = 5;

        when(daoMock.annullaPrenotazioneCliente(idPreno, idCliente)).thenReturn(false);

        boolean esito = service.annullaPrenotazioneCliente(idPreno, idCliente, sessionMock);

        assertFalse(esito);
        verify(sessionMock, never()).removeAttribute(anyString());
    }

    @Test
    void testAnnullaPrenotazioneCliente_SessioneNull_NonCrasha() throws Exception {
        when(daoMock.annullaPrenotazioneCliente(anyInt(), anyInt())).thenReturn(true);

        assertDoesNotThrow(() ->
            service.annullaPrenotazioneCliente(10, 5, null)
        );
    }

    @Test
    void testAnnullaPrenotazioneCliente_ErrorWrapping() throws Exception {
        when(daoMock.annullaPrenotazioneCliente(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("SQL Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.annullaPrenotazioneCliente(10, 5, sessionMock));

        assertEquals("Errore annullamento prenotazione", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}