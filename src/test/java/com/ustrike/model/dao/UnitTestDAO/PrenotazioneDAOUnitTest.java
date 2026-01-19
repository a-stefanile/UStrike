package com.ustrike.model.dao.UnitTestDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.PrenotazioneView;
import com.ustrike.util.DBConnection;

class PrenotazioneDAOUnitTest {

    private PrenotazioneDAO dao;
    private MockedStatic<DBConnection> mockedDb;

    @Mock private Connection conn;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dao = new PrenotazioneDAO();
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    void tearDown() {
        mockedDb.close();
    }

    // --- GOOD PATHS ---

    @Test
    void testInsertPrenotazione_ConStaffENote() throws Exception {
        Timestamp ora = new Timestamp(System.currentTimeMillis());
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(777);

        int id = dao.insertPrenotazione(ora, ora, "Confermata", "4", 1, 1, 10, 2, "Nota test");

        assertEquals(777, id);
        verify(ps).setInt(8, 2);      // Verifica IDStaff impostato
        verify(ps).setString(9, "Nota test"); // Verifica NoteStaff impostate
    }

    @Test
    void testAnnullaPrenotazioneCliente_Successo() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1); // Simula update avvenuto

        boolean esito = dao.annullaPrenotazioneCliente(500, 10);

        assertTrue(esito);
        verify(ps).setInt(1, 500); // idPrenotazione
        verify(ps).setInt(2, 10);  // idCliente
    }

    @Test
    void testSelectPrenotazioniByClienteView_MappaturaCorretta() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        
        // Simula una riga nel ResultSet
        when(rs.next()).thenReturn(true, false); 
        when(rs.getInt("IDPrenotazione")).thenReturn(1);
        when(rs.getString("NomeServizio")).thenReturn("Bowling");
        when(rs.getInt("CapacitaRisorsa")).thenReturn(6);
        when(rs.getInt("IDStaff")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true); // Fondamentale per testare la tua logica wasNull()

        List<PrenotazioneView> result = dao.selectPrenotazioniByClienteView(10);

        assertFalse(result.isEmpty());
        PrenotazioneView v = result.get(0);
        assertEquals("Bowling", v.getNomeServizio());
        assertNull(v.getIDStaff()); // Verifica che wasNull() abbia funzionato
    }

    // --- BAD PATHS & EDGE CASES ---

    @Test
    void testInsertPrenotazione_GestioneValoriNull() throws Exception {
        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        // Chiamata con staff e note null
        dao.insertPrenotazione(new Timestamp(0), new Timestamp(0), "Stato", "P", 1, 1, 1, null, null);

        // Verifichiamo che il DAO usi correttamente Types.INTEGER e Types.VARCHAR per i null
        verify(ps).setNull(8, Types.INTEGER);
        verify(ps).setNull(9, Types.VARCHAR);
    }

    @Test
    void testAnnullaPrenotazioneCliente_GiaInLavorazione() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        // Se la prenotazione è già "Confermata", la clausola WHERE StatoPrenotazione = 'In attesa'
        // farà sì che l'update restituisca 0 righe colpite.
        when(ps.executeUpdate()).thenReturn(0);

        boolean esito = dao.annullaPrenotazioneCliente(500, 10);

        assertFalse(esito, "Non deve annullare se lo stato non è 'In attesa'");
    }

    @Test
    void testUpdateStatoPrenotazione_NullSafety() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        // Testiamo l'update passando parametri null per note e staff
        boolean ok = dao.updateStatoPrenotazione(1, "Rifiutata", null, "");

        assertTrue(ok);
        verify(ps).setNull(2, Types.INTEGER); // IDStaff null
        verify(ps).setNull(3, Types.VARCHAR); // NoteStaff null (perché stringa vuota)
    }
}