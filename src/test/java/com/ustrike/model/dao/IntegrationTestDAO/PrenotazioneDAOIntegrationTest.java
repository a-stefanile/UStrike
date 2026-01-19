package com.ustrike.model.dao.IntegrationTestDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class PrenotazioneDAOIntegrationTest {

    private PrenotazioneDAO prenotazioneDao;
    private ServizioDAO servizioDao;
    private RisorsaDAO risorsaDao;
    private ClienteDAO clienteDao;
    private StaffDAO staffDao;

    // ID necessari per mantenere l'integrità referenziale
    private int idServizio, idRisorsa, idCliente, idStaff, idPrenotazione;

    @BeforeEach
    void setUp() throws SQLException {
        prenotazioneDao = new PrenotazioneDAO();
        servizioDao = new ServizioDAO();
        risorsaDao = new RisorsaDAO();
        clienteDao = new ClienteDAO();
        staffDao = new StaffDAO();

        // 1. Creiamo il Servizio
        Servizio s = new Servizio(0, "Bowling_Prenotazione_Test", true);
        servizioDao.doSave(s);
        idServizio = servizioDao.doRetrieveByNome("Bowling_Prenotazione_Test").getIDServizio();

        // 2. Creiamo la Risorsa
        idRisorsa = risorsaDao.insertRisorsa(1, 6, idServizio);

        // 3. Creiamo il Cliente
        Cliente c = new Cliente(0, "Mario", "Rossi", "mario.prenota@test.it", "hash", 0);
        clienteDao.insertCliente(c);
        idCliente = c.getIDCliente();

        // 4. Creiamo lo Staff
        Staff st = new Staff();
        st.setNomeStaff("Anna");
        st.setCognomeStaff("Verdi");
        st.setEmail("anna.staff@test.it");
        st.setPasswordHash("hash");
        st.setRuolo("Bowling");
        staffDao.doSave(st);
        idStaff = st.getIDStaff();

        idPrenotazione = -1; // Reset per ogni test
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (idPrenotazione != -1) {
            prenotazioneDao.doDelete(idPrenotazione);
        }
        if (idRisorsa != -1) {
            risorsaDao.deleteRisorsa(idRisorsa);
        }
        if (idServizio != -1) {
            servizioDao.doDelete(idServizio);
        }
        if (idCliente != -1) {
            clienteDao.deleteCliente(idCliente);
        }
        if (idStaff != -1) {
            staffDao.doDelete(idStaff);
        }
    }
    @Test
    void testLifecyclePrenotazione_Successo() throws SQLException {
        Timestamp dataOra = Timestamp.valueOf("2026-05-20 20:00:00");
        
        // 1. INSERT (Stato iniziale: 'In attesa')
        idPrenotazione = prenotazioneDao.insertPrenotazione(
            dataOra, dataOra, "In attesa", "4 persone", 
            idServizio, idRisorsa, idCliente, null, null
        );
        assertTrue(idPrenotazione > 0);

        // 2. SELECT
        Prenotazione recuperata = prenotazioneDao.selectPrenotazione(idPrenotazione);
        assertNotNull(recuperata);
        assertEquals("In attesa", recuperata.getStatoPrenotazione());

        // 3. UPDATE STATO (Simuliamo l'accettazione da parte dello staff)
        boolean updated = prenotazioneDao.updateStatoPrenotazione(
            idPrenotazione, "Confermata", idStaff, "Tutto ok, pista pronta"
        );
        assertTrue(updated, "Lo staff dovrebbe poter confermare una prenotazione in attesa");

        // 4. VERIFY UPDATE
        Prenotazione check = prenotazioneDao.selectPrenotazione(idPrenotazione);
        assertEquals("Confermata", check.getStatoPrenotazione());
        assertEquals(idStaff, check.getIDStaff());
        assertEquals("Tutto ok, pista pronta", check.getNoteStaff());
    }

 // --- BAD PATH ---
    @Test
    void testUpdateStato_GiaLavorata() throws SQLException {
        // Creiamo e confermiamo subito
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        idPrenotazione = prenotazioneDao.insertPrenotazione(ora, ora, "In attesa", "2", idServizio, idRisorsa, idCliente, null, null);
        
        prenotazioneDao.updateStatoPrenotazione(idPrenotazione, "Confermata", idStaff, "OK");

        // Tentiamo di ri-aggiornare una prenotazione che non è più 'In attesa'
        // La tua query ha: WHERE IDPrenotazione = ? AND StatoPrenotazione = 'In attesa'
        boolean secondoUpdate = prenotazioneDao.updateStatoPrenotazione(idPrenotazione, "Rifiutata", idStaff, "Cambio idea");
        
        assertFalse(secondoUpdate, "Non si dovrebbe poter modificare una prenotazione già confermata o rifiutata");
    }

    

    @Test
    void testInsertPrenotazione_ClienteInesistente() {
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        // IDCliente = -1 non esiste
        assertThrows(SQLException.class, () -> {
            prenotazioneDao.insertPrenotazione(ora, ora, "In attesa", "1", idServizio, idRisorsa, -1, null, null);
        }, "Il database deve lanciare violazione di FK per cliente inesistente");
    }
}