package com.ustrike.model.dao.IntegrationTestDAO;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.*;
import com.ustrike.model.dto.*;

class PrenotazioneDAOIntegrationTest {

    private PrenotazioneDAO prenotazioneDao;
    private ServizioDAO servizioDao;
    private RisorsaDAO risorsaDao;
    private ClienteDAO clienteDao;
    private StaffDAO staffDao;

    private int idServizio, idRisorsa, idCliente, idStaff, idPrenotazione;

    @BeforeEach
    void setUp() throws SQLException {
        prenotazioneDao = new PrenotazioneDAO();
        servizioDao = new ServizioDAO();
        risorsaDao = new RisorsaDAO();
        clienteDao = new ClienteDAO();
        staffDao = new StaffDAO();

        // 1. Setup Gerarchia
        Servizio s = new Servizio(0, "Bowling_Test_Integration", true);
        servizioDao.doSave(s);
        idServizio = servizioDao.doRetrieveByNome("Bowling_Test_Integration").getIDServizio();

        idRisorsa = risorsaDao.insertRisorsa(1, 6, idServizio);

        Cliente c = new Cliente(0, "Mario", "Rossi", "mario.test@integration.it", "hash", 0);
        clienteDao.insertCliente(c);
        idCliente = c.getIDCliente();

        Staff st = new Staff();
        st.setNomeStaff("Anna");
        st.setCognomeStaff("Verdi");
        st.setEmail("anna.staff@integration.it");
        st.setPasswordHash("hash");
        st.setRuolo("Bowling");
        staffDao.doSave(st);
        idStaff = st.getIDStaff();

        idPrenotazione = -1;
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Pulizia speculare all'inserimento
        if (idPrenotazione != -1) prenotazioneDao.doDelete(idPrenotazione);
        if (idRisorsa != -1) risorsaDao.deleteRisorsa(idRisorsa);
        if (idServizio != -1) servizioDao.doDelete(idServizio);
        if (idCliente != -1) clienteDao.deleteCliente(idCliente);
        if (idStaff != -1) staffDao.doDelete(idStaff);
    }

    @Test
    void testLifecycleEAnnullamentoCliente() throws SQLException {
        Timestamp dataOra = Timestamp.valueOf("2026-06-10 18:00:00");

        // 1. Inserimento con parametri NULL (Staff e Note)
        idPrenotazione = prenotazioneDao.insertPrenotazione(
            dataOra, dataOra, "In attesa", "2 persone", 
            idServizio, idRisorsa, idCliente, null, null
        );
        assertTrue(idPrenotazione > 0);

        // 2. Verifica tramite selectPrenotazioniByClienteView (Test della JOIN)
        try {
            List<PrenotazioneView> views = prenotazioneDao.selectPrenotazioniByClienteView(idCliente);
            assertFalse(views.isEmpty());
            assertEquals("Bowling_Test_Integration", views.get(0).getNomeServizio());
            assertNull(views.get(0).getIDStaff()); // Verifica gestione NULL
        } catch (Exception e) {
            fail("Errore nella select della View: " + e.getMessage());
        }

        // 3. Test nuovo metodo: annullaPrenotazioneCliente
        boolean annullata = prenotazioneDao.annullaPrenotazioneCliente(idPrenotazione, idCliente);
        assertTrue(annullata, "Il cliente dovrebbe poter annullare una propria prenotazione in attesa");

        // 4. Verifica stato cambiato
        Prenotazione p = prenotazioneDao.selectPrenotazione(idPrenotazione);
        assertEquals("Annullata", p.getStatoPrenotazione());
    }

    @Test
    void testUpdateStato_ConNoteEStaff() throws SQLException {
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        idPrenotazione = prenotazioneDao.insertPrenotazione(ora, ora, "In attesa", "5", idServizio, idRisorsa, idCliente, null, null);

        // Update completo (simulando lavoro dello staff)
        boolean ok = prenotazioneDao.updateStatoPrenotazione(idPrenotazione, "Confermata", idStaff, "Pista assegnata");
        assertTrue(ok);

        Prenotazione p = prenotazioneDao.selectPrenotazione(idPrenotazione);
        assertEquals("Confermata", p.getStatoPrenotazione());
        assertEquals(idStaff, p.getIDStaff());
        assertEquals("Pista assegnata", p.getNoteStaff());
    }

    @Test
    void testAnnullamento_FallisceSeGiaConfermata() throws SQLException {
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        idPrenotazione = prenotazioneDao.insertPrenotazione(ora, ora, "In attesa", "1", idServizio, idRisorsa, idCliente, null, null);
        
        // Lo staff la conferma
        prenotazioneDao.updateStatoPrenotazione(idPrenotazione, "Confermata", idStaff, "OK");

        // Il cliente prova ad annullarla (deve fallire perché non è più 'In attesa')
        boolean esito = prenotazioneDao.annullaPrenotazioneCliente(idPrenotazione, idCliente);
        assertFalse(esito, "Non si può annullare una prenotazione già confermata dallo staff");
    }
}