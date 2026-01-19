package com.ustrike.model.service.UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dto.Risorsa;
import com.ustrike.model.service.RisorsaService;

class RisorsaServiceUnitTest {

    private RisorsaService service;

    @Mock
    private RisorsaDAO daoMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RisorsaService(daoMock);
    }

    @Test
    void testAggiornaStatoRisorsa_Successo() throws Exception {
        int idRisorsa = 1;
        int nuovoStato = 2;
        int capacitaAttuale = 10;


        Risorsa rFinta = new Risorsa();
        rFinta.setIDRisorsa(idRisorsa);
        rFinta.setCapacita(capacitaAttuale);


        when(daoMock.selectRisorsa(idRisorsa)).thenReturn(rFinta);
        when(daoMock.updateRisorsa(idRisorsa, nuovoStato, capacitaAttuale)).thenReturn(true);

  
        boolean risultato = service.aggiornaStatoRisorsa(idRisorsa, nuovoStato);


        assertTrue(risultato);
        verify(daoMock).selectRisorsa(idRisorsa);
        verify(daoMock).updateRisorsa(idRisorsa, nuovoStato, capacitaAttuale);
    }

    @Test
    void testAggiornaStatoRisorsa_RisorsaInesistente() throws Exception {
        int idRisorsa = 999;

        when(daoMock.selectRisorsa(idRisorsa)).thenReturn(null);


        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.aggiornaStatoRisorsa(idRisorsa, 1);
        });

        assertEquals("Risorsa ID 999 inesistente", ex.getMessage());

        verify(daoMock, never()).updateRisorsa(anyInt(), anyInt(), anyInt());
    }

    @Test
    void testGetTutteLeRisorse_ErroreDB() throws Exception {

        when(daoMock.selectAllRisorse()).thenThrow(new RuntimeException("SQL Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.getTutteLeRisorse();
        });

        assertEquals("Errore caricamento risorse", ex.getMessage());
    }
}