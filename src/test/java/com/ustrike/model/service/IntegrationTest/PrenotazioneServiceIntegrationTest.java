package com.ustrike.model.service.IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Cliente;
import com.ustrike.model.dto.Prenotazione;
import com.ustrike.model.dto.Servizio;
import com.ustrike.model.dto.Staff;
import com.ustrike.model.service.PrenotazioneService;

import jakarta.servlet.http.HttpSession;

class PrenotazioneServiceIntegrationTest {

    private PrenotazioneService prenotazioneService;
    private PrenotazioneDAO prenotazioneDAO;
    private ClienteDAO clienteDAO;
    private StaffDAO staffDAO;
    private RisorsaDAO risorsaDAO;
    private ServizioDAO servizioDAO;
    private HttpSession session;

    // ID per setup e pulizia
    private int idClienteTest;
    private int idStaffTest;
    private int idServizioTest;
    private int idRisorsaTest;
    private int idPrenotazioneTest = -1;

    @BeforeEach
    void setUp() throws SQLException {
        prenotazioneDAO = new PrenotazioneDAO();
        clienteDAO = new ClienteDAO();
        staffDAO = new StaffDAO();
        risorsaDAO = new RisorsaDAO();
        servizioDAO = new ServizioDAO();
        prenotazioneService = new PrenotazioneService(prenotazioneDAO);
        
        session = mock(HttpSession.class);

        // 1. Setup Servizio (necessario per Risorsa e Prenotazione)
        Servizio s = new Servizio();
        s.setNomeServizio("Bowling");
        s.setStatoServizio(true);
        servizioDAO.doSave(s);
        idServizioTest = servizioDAO.doRetrieveByNome("Bowling").getIDServizio();

        // 2. Setup Risorsa
        idRisorsaTest = risorsaDAO.insertRisorsa(1, 6, idServizioTest);

        // 3. Setup Cliente (Tutti i campi NOT NULL popolati)
        Cliente c = new Cliente(0, "Luca", "Verdi", "luca.verdi@test.it", "hash_di_prova", 0);
        clienteDAO.insertCliente(c);
        idClienteTest = c.getIDCliente();

        // 4. Setup Staff (Ruolo deve essere 'Bowling' o 'GoKart')
        Staff st = new Staff();
        st.setNomeStaff("Anna");
        st.setCognomeStaff("Bianchi"); // NOT NULL
        st.setEmail("anna.staff@test.it");
        st.setPasswordHash("hash_staff_prova"); // NOT NULL
        st.setRuolo("Bowling"); // Deve corrispondere all'ENUM del tuo DB
        
        staffDAO.doSave(st);
        idStaffTest = st.getIDStaff();
    }
    @AfterEach
    void tearDown() throws SQLException {
        // Pulizia (ordine inverso per via delle FK)
        if (idPrenotazioneTest != -1) {
            prenotazioneDAO.doDelete(idPrenotazioneTest);
        }
        clienteDAO.deleteCliente(idClienteTest);
        staffDAO.doDelete(idStaffTest);
        risorsaDAO.deleteRisorsa(idRisorsaTest);
        servizioDAO.doDelete(idServizioTest);
    }

    // --- GOOD PATHS ---

    @Test
    void testCreaPrenotazione_Successo_E_CacheInvalidation() {
        Timestamp data = Timestamp.valueOf("2026-06-20 00:00:00");
        Timestamp ora = Timestamp.valueOf("2026-06-20 21:00:00");

        idPrenotazioneTest = prenotazioneService.creaPrenotazione(
                data, ora, "4 persone", idServizioTest, idRisorsaTest, idClienteTest, session
        );

        assertTrue(idPrenotazioneTest > 0);
        // Verifica che la cache sia stata pulita
        verify(session, atLeastOnce()).removeAttribute("prenotazioni_" + idClienteTest);
    }

    @Test
    void testAccettaPrenotazione_Successo() throws SQLException {
        idPrenotazioneTest = prenotazioneService.creaPrenotazione(
                Timestamp.valueOf("2026-06-20 00:00:00"), Timestamp.valueOf("2026-06-20 22:00:00"), 
                "2 persone", idServizioTest, idRisorsaTest, idClienteTest, null
        );

        boolean ok = prenotazioneService.accettaPrenotazione(idPrenotazioneTest, idStaffTest, session);

        assertTrue(ok);
        Prenotazione p = prenotazioneDAO.selectPrenotazione(idPrenotazioneTest);
        assertEquals("Confermata", p.getStatoPrenotazione()); // Corretto getter
    }

    @Test
    void testAnnullaPrenotazioneCliente_Successo() throws SQLException {
        idPrenotazioneTest = prenotazioneService.creaPrenotazione(
                Timestamp.valueOf("2026-06-20 00:00:00"), Timestamp.valueOf("2026-06-20 23:00:00"), 
                "1 persona", idServizioTest, idRisorsaTest, idClienteTest, null
        );

        boolean ok = prenotazioneService.annullaPrenotazioneCliente(idPrenotazioneTest, idClienteTest, session);

        assertTrue(ok);
        Prenotazione p = prenotazioneDAO.selectPrenotazione(idPrenotazioneTest);
        assertEquals("Annullata", p.getStatoPrenotazione());
    }

    // --- BAD PATHS ---

    @Test
    void testAccettaPrenotazione_GiaProcessata_Fallimento() throws SQLException {
        // Creazione e rifiuto immediato
        idPrenotazioneTest = prenotazioneService.creaPrenotazione(
                Timestamp.valueOf("2026-06-20 00:00:00"), Timestamp.valueOf("2026-06-20 10:00:00"), 
                "2 persone", idServizioTest, idRisorsaTest, idClienteTest, null
        );
        prenotazioneService.rifiutaPrenotazione(idPrenotazioneTest, idStaffTest, "Rifiuto", null);

        // Tentativo di accettare una prenotazione già rifiutata
        boolean ok = prenotazioneService.accettaPrenotazione(idPrenotazioneTest, idStaffTest, session);

        assertFalse(ok, "Non deve permettere di accettare una prenotazione che non è più 'In attesa'");
    }

    @Test
    void testAnnullaPrenotazione_ProprietarioErrato_Fallimento() {
        idPrenotazioneTest = prenotazioneService.creaPrenotazione(
                Timestamp.valueOf("2026-06-20 00:00:00"), Timestamp.valueOf("2026-06-20 11:00:00"), 
                "2 persone", idServizioTest, idRisorsaTest, idClienteTest, null
        );

        // ID Cliente 9999 non è il proprietario (che è idClienteTest)
        boolean ok = prenotazioneService.annullaPrenotazioneCliente(idPrenotazioneTest, 9999, session);

        assertFalse(ok, "Un utente non può annullare la prenotazione di qualcun altro");
    }

    @Test
    void testCreaPrenotazione_ViolazioneVincoliDB_Eccezione() {
        // Test di robustezza: ID risorsa inesistente
        assertThrows(RuntimeException.class, () -> {
            prenotazioneService.creaPrenotazione(
                Timestamp.valueOf("2026-06-20 00:00:00"), Timestamp.valueOf("2026-06-20 12:00:00"), 
                "2 persone", idServizioTest, -1, idClienteTest, null
            );
        });
    }
}