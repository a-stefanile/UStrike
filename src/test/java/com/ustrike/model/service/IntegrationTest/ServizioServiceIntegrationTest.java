package com.ustrike.model.service.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Servizio;
import com.ustrike.model.service.ServizioService;

class ServizioServiceIntegrationTest {

    private ServizioService servizioService;
    private ServizioDAO servizioDAO;
    private int idServizioTest = -1;

    @BeforeEach
    void setUp() throws SQLException {
        System.out.println("--- Setup ServizioService Integration ---");
        servizioDAO = new ServizioDAO();
        servizioService = new ServizioService(servizioDAO);
        
        // Creiamo un servizio di test (StatoServizio = 0, disabilitato)
        Servizio s = new Servizio();
        s.setNomeServizio("Servizio_Integrazione_Test");
        s.setStatoServizio(false);
        
        // Lo salviamo nel DB reale tramite DAO
        servizioDAO.doSave(s);
        
        // Recuperiamo l'ID generato per la pulizia successiva
        Servizio salvato = servizioDAO.doRetrieveByNome("Servizio_Integrazione_Test");
        if (salvato != null) {
            idServizioTest = salvato.getIDServizio();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Pulizia: eliminiamo il servizio creato per il test
        if (idServizioTest != -1) {
            servizioDAO.doDelete(idServizioTest);
            System.out.println("Pulizia effettuata per ID: " + idServizioTest);
        }
    }

    @Test
    void testAbilitaServizio_Successo_Integrato() {
        System.out.println("Testando abilitaServizio...");
        
        // 1. Verifichiamo che inizialmente non sia tra quelli abilitati
        List<Servizio> iniziali = servizioService.getServiziAbilitati();
        boolean presenteInizialmente = iniziali.stream()
                .anyMatch(s -> s.getIDServizio() == idServizioTest);
        assertFalse(presenteInizialmente, "Il servizio non dovrebbe essere abilitato inizialmente");

        // 2. Chiamata al Service per abilitare
        boolean ok = servizioService.abilitaServizio(idServizioTest);
        assertTrue(ok, "L'abilitazione tramite service dovrebbe restituire true");

        // 3. Verifica persistenza sul DB
        Servizio aggiornato = servizioService.getServizioById(idServizioTest);
        assertTrue(aggiornato.getStatoServizio(), "Lo stato nel DB dovrebbe essere ora 1 (true)");

        // 4. Verifica che ora appaia nella lista per i clienti
        List<Servizio> abilitati = servizioService.getServiziAbilitati();
        assertTrue(abilitati.stream().anyMatch(s -> s.getIDServizio() == idServizioTest), 
                   "Il servizio dovrebbe ora apparire nel catalogo abilitati");
    }

    @Test
    void testDisabilitaServizio_Successo_Integrato() {
        System.out.println("Testando disabilitaServizio...");
        
        // Prepariamo il servizio come abilitato
        servizioDAO.abilitaServizio(idServizioTest);

        // Chiamata al Service per disabilitare
        boolean ok = servizioService.disabilitaServizio(idServizioTest);
        assertTrue(ok);

        // Verifica che non appaia più nella lista abilitati
        List<Servizio> abilitati = servizioService.getServiziAbilitati();
        assertFalse(abilitati.stream().anyMatch(s -> s.getIDServizio() == idServizioTest));
    }

    
    @Test
    void testAbilitaServizio_Inesistente_BadPath() {
        System.out.println("Testando abilitaServizio su ID inesistente...");
        
        // Tentativo su un ID che non esiste (es. -1)
        assertThrows(IllegalArgumentException.class, () -> {
            servizioService.abilitaServizio(-1);
        }, "Il service deve lanciare IllegalArgumentException se il servizio non esiste");
    }
}