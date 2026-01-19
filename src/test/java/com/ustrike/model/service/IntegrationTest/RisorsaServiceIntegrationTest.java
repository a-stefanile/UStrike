package com.ustrike.model.service.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Risorsa;
import com.ustrike.model.dto.Servizio;
import com.ustrike.model.service.RisorsaService;

class RisorsaServiceIntegrationTest {

    private RisorsaService risorsaService;
    private RisorsaDAO risorsaDAO;
    private ServizioDAO servizioDAO;

    private int idServizioPadre = -1;
    private int idRisorsaTest = -1;

    @BeforeEach
    void setUp() throws SQLException {
        System.out.println("--- Setup RisorsaService Integration ---");
        risorsaDAO = new RisorsaDAO();
        servizioDAO = new ServizioDAO();
        risorsaService = new RisorsaService(risorsaDAO);

        // 1. Dobbiamo creare un Servizio reale perché Risorsa ha una FK su Servizio
        Servizio s = new Servizio();
        s.setNomeServizio("Bowling_Per_Test_Risorsa");
        s.setStatoServizio(true);
        servizioDAO.doSave(s);
        
        idServizioPadre = servizioDAO.doRetrieveByNome("Bowling_Per_Test_Risorsa").getIDServizio();

        // 2. Creiamo una Risorsa di base (Pista 1)
        // Stato 1 = Disponibile, Capacità 6 persone
        idRisorsaTest = risorsaService.creaRisorsa(1, 6, idServizioPadre);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Pulizia a ritroso: prima la Risorsa, poi il Servizio (per via dei vincoli FK)
        if (idRisorsaTest != -1) {
            risorsaDAO.deleteRisorsa(idRisorsaTest);
        }
        if (idServizioPadre != -1) {
            servizioDAO.doDelete(idServizioPadre);
        }
        System.out.println("Database pulito.");
    }

    @Test
    void testCheckDisponibilitaReale_SenzaPrenotazioni() {
        System.out.println("Testando disponibilità su risorsa libera...");
        
        // Simuliamo un controllo per domani alle 20:00
        Timestamp domani = Timestamp.valueOf("2026-01-20 20:00:00");
        
        // Non essendoci prenotazioni nel DB per questa risorsa/ora, deve tornare true
        boolean disponibile = risorsaService.isRisorsaDisponibile(idRisorsaTest, domani);
        
        assertTrue(disponibile, "La risorsa dovrebbe risultare disponibile se non ci sono prenotazioni");
    }

    @Test
    void testAggiornaStatoRisorsa_E_VerificaFiltro() {
        System.out.println("Testando cambio stato risorsa (Guasto/Attiva)...");

        // 1. Mettiamo la risorsa in stato '0' (Guasta/Manutenzione)
        boolean ok = risorsaService.aggiornaStatoRisorsa(idRisorsaTest, 0);
        assertTrue(ok);

        // 2. Verifichiamo che la query per i clienti non la restituisca più
        List<Risorsa> libere = risorsaService.getRisorseLibereByServizio(idServizioPadre);
        boolean presente = libere.stream().anyMatch(r -> r.getIDRisorsa() == idRisorsaTest);
        
        assertFalse(presente, "Una risorsa guasta non deve apparire tra quelle prenotabili");
    }

    @Test
    void testVerificaAppartenenzaServizio() {
        System.out.println("Testando legame logico Risorsa-Servizio...");
        
        // Test positivo
        assertTrue(risorsaService.risorsaAppartieneAlServizio(idRisorsaTest, idServizioPadre));
        
        // Test negativo (con un ID servizio che non esiste o sbagliato)
        assertFalse(risorsaService.risorsaAppartieneAlServizio(idRisorsaTest, 9999));
    }

    @Test
    void testAggiornaStato_RisorsaInesistente_BadPath() {
        System.out.println("Testando errore su risorsa inesistente...");
        
        assertThrows(IllegalArgumentException.class, () -> {
            risorsaService.aggiornaStatoRisorsa(-1, 1);
        });
    }
}