package com.ustrike.model.dao.IntegrationTestDAO;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Risorsa;
import com.ustrike.model.dto.Servizio;
import java.sql.SQLException;
import java.util.List;

class RisorsaDAOIntegrationTest {

    private RisorsaDAO risorsaDao;
    private ServizioDAO servizioDao;
    
    private int idServizioPadre = -1;
    private int idRisorsaTest = -1;
    private final String NOME_SERVIZIO_TEST = "Servizio_Test_Risorsa";

    @BeforeEach
    void setUp() throws SQLException {
        risorsaDao = new RisorsaDAO();
        servizioDao = new ServizioDAO();

        // 1. Creiamo un servizio reale per soddisfare la Foreign Key
        Servizio s = new Servizio(0, NOME_SERVIZIO_TEST, true);
        servizioDao.doSave(s);
        
        // Recuperiamo l'ID generato dal DB
        Servizio recuperato = servizioDao.doRetrieveByNome(NOME_SERVIZIO_TEST);
        idServizioPadre = recuperato.getIDServizio();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // IMPORTANTE: Ordine di cancellazione (Bottom-Up al contrario)
        // Prima eliminiamo la risorsa (figlia), poi il servizio (padre)
        if (idRisorsaTest != -1) {
            risorsaDao.deleteRisorsa(idRisorsaTest);
        }
        if (idServizioPadre != -1) {
            servizioDao.doDelete(idServizioPadre);
        }
    }

    // --- GOOD PATH ---

    @Test
    void testLifecycleRisorsa_Successo() throws SQLException {
        // 1. Inserimento Risorsa
        // stato=1 (attiva), capacita=5, idServizio=quello creato nel setUp
        idRisorsaTest = risorsaDao.insertRisorsa(1, 5, idServizioPadre);
        assertTrue(idRisorsaTest > 0, "L'inserimento della risorsa dovrebbe restituire un ID valido");

        // 2. Lettura (Select)
        Risorsa recuperata = risorsaDao.selectRisorsa(idRisorsaTest);
        assertNotNull(recuperata);
        assertEquals(5, recuperata.getCapacita());
        assertEquals(idServizioPadre, recuperata.getIDServizio());

        // 3. Aggiornamento (Update)
        boolean updated = risorsaDao.updateRisorsa(idRisorsaTest, 0, 10);
        assertTrue(updated);

        // 4. Verifica Update
        Risorsa check = risorsaDao.selectRisorsa(idRisorsaTest);
        assertEquals(0, check.getStato(), "Lo stato dovrebbe essere 0");
        assertEquals(10, check.getCapacita(), "La capacità dovrebbe essere 10");
    }

    @Test
    void testSelectRisorseByServizio() throws SQLException {
        // Inseriamo un paio di risorse per lo stesso servizio
        risorsaDao.insertRisorsa(1, 2, idServizioPadre);
        int idSeconda = risorsaDao.insertRisorsa(1, 8, idServizioPadre);
        
        List<Risorsa> lista = risorsaDao.selectRisorseByServizio(idServizioPadre);
        
        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 2);
        
        // Pulizia manuale della risorsa extra creata in questo test
        risorsaDao.deleteRisorsa(idSeconda);
    }

    // --- BAD PATH ---

    @Test
    void testInsertRisorsa_ServizioInesistente() {
        // Proviamo a collegare una risorsa a un ID servizio che non esiste
        // Questo violerà il vincolo fk_risorsa_servizio
        int idFinto = 99999;
        
        assertThrows(SQLException.class, () -> {
            risorsaDao.insertRisorsa(1, 4, idFinto);
        }, "Il DB deve impedire la creazione di una risorsa legata a un servizio inesistente");
    }

    @Test
    void testDeleteServizioConRisorsa() throws SQLException {
        // Creiamo una risorsa
        idRisorsaTest = risorsaDao.insertRisorsa(1, 4, idServizioPadre);
        
        // Tentiamo di eliminare il servizio PADRE mentre ha ancora una risorsa FIGLIA        // Nota: Poiché doDelete cattura l'eccezione internamente e torna false, usiamo assertFalse
        boolean eliminato = servizioDao.doDelete(idServizioPadre);
        
        assertFalse(eliminato, "Il DB non deve permettere l'eliminazione di un servizio se ha risorse collegate");
    }
}