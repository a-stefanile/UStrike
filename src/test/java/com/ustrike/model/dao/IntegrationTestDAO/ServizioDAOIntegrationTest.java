package com.ustrike.model.dao.IntegrationTestDAO;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Servizio;

class ServizioDAOIntegrationTest {

    private ServizioDAO dao;
    private final String NOME_TEST = "Bowling_Integrazione";
    private int idDaCancellare = -1; // Memorizza l'ID per una pulizia sicura

    @BeforeEach
    void setUp() {
        dao = new ServizioDAO();
        // Pulizia preventiva basata sul nome in caso di test interrotti precedentemente
        Servizio s = dao.doRetrieveByNome(NOME_TEST);
        if (s != null) dao.doDelete(s.getIDServizio());
        
        Servizio sMod = dao.doRetrieveByNome(NOME_TEST + "_Mod");
        if (sMod != null) dao.doDelete(sMod.getIDServizio());
    }

    @AfterEach
    void tearDown() {
        // Pulizia basata su ID: è il metodo più sicuro perché l'ID non cambia mai
        if (idDaCancellare != -1) {
            dao.doDelete(idDaCancellare);
            idDaCancellare = -1; // Reset per il prossimo test
        }
    }

    // --- GOOD PATH (Percorso Felice) ---

    @Test
    void testLifecycleSuccesso() {
        // 1. Inserimento
        Servizio s = new Servizio(0, NOME_TEST, true);
        assertTrue(dao.doSave(s), "Il salvataggio dovrebbe riuscire");

        // 2. Recupero per ottenere l'ID reale generato dal DB
        Servizio recuperato = dao.doRetrieveByNome(NOME_TEST);
        assertNotNull(recuperato);
        idDaCancellare = recuperato.getIDServizio(); // <--- Fondamentale per il tearDown

        // 3. Aggiornamento (Cambiamo il nome, ma l'ID resta lo stesso)
        recuperato.setNomeServizio(NOME_TEST + "_Mod");
        assertTrue(dao.doUpdate(recuperato), "L'update dovrebbe riuscire");

        // 4. Verifica finale
        Servizio check = dao.doRetrieveByKey(idDaCancellare);
        assertEquals(NOME_TEST + "_Mod", check.getNomeServizio());
    }

    // --- BAD PATH (Percorso Negativo) ---

    @Test
    void testSaveNomeNull() {
        // Lo schema SQL ha NOT NULL su NomeServizio
        Servizio s = new Servizio();
        s.setNomeServizio(null); 
        s.setStatoServizio(true);

        boolean result = dao.doSave(s);
        
        // Il DAO intercetta la SQLException di MySQL e ritorna false
        assertFalse(result, "Il database dovrebbe rifiutare un nome NULL");
    }

    @Test
    void testSaveNomeTroppoLungo() {
        // Lo schema SQL ha VARCHAR(52). Testiamo con 60+ caratteri.
        String nomeLungo = "Questo_Nome_E_Decisamente_Troppo_Lungo_Per_Il_Campo_VARCHAR_52_Del_Database";
        Servizio s = new Servizio(0, nomeLungo, true);

        boolean result = dao.doSave(s);
        
        // MySQL restituisce un errore di "Data truncation"
        assertFalse(result, "Il database dovrebbe rifiutare nomi che superano i 52 caratteri");
    }

    @Test
    void testRetrieveInesistente() {
        // Test di lettura su ID che non può esistere
        Servizio s = dao.doRetrieveByKey(-1);
        assertNull(s, "La ricerca di un ID inesistente deve restituire null");
    }

    @Test
    void testUpdateInesistente() {
        // Proviamo ad aggiornare un servizio con ID fittizio molto alto
        Servizio s = new Servizio(99999, "Fake", true);
        boolean result = dao.doUpdate(s);
        
        assertFalse(result, "L'update su un record inesistente deve restituire false");
    }
}