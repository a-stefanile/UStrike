package com.ustrike.model.dao.UnitTestDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
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

    // --- TEST POSITIVI ---

    @Test
    void testUpdateStatoPrenotazione_Successo() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1); // Una riga aggiornata

        // Act
        boolean result = dao.updateStatoPrenotazione(10, "Confermata", 1, "Tutto ok");

        // Assert
        assertTrue(result);
        verify(ps).setString(1, "Confermata");
        verify(ps).setInt(2, 1);
        verify(ps).setString(3, "Tutto ok");
        verify(ps).setInt(4, 10);
    }

    @Test
    void testSelectPrenotazione_Trovata() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("IDPrenotazione")).thenReturn(100);
        when(rs.getString("StatoPrenotazione")).thenReturn("In attesa");
        when(rs.getInt("IDStaff")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true); // Simula IDStaff NULL nel DB

        // Act
        Prenotazione p = dao.selectPrenotazione(100);

        // Assert
        assertNotNull(p);
        assertEquals(100, p.getIDPrenotazione());
        assertNull(p.getIDStaff(), "IDStaff dovrebbe essere null se il DB torna NULL");
    }

    // --- TEST NEGATIVI E EDGE CASES ---

    @Test
    void testUpdateStatoPrenotazione_GiaGestita() throws Exception {
        // Arrange: Se la riga non è più "In attesa", executeUpdate tornerà 0
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        // Act
        boolean result = dao.updateStatoPrenotazione(10, "Confermata", 1, null);

        // Assert
        assertFalse(result, "Dovrebbe fallire se la prenotazione non è più 'In attesa'");
    }

    @Test
    void testInsertPrenotazione_GestioneNull() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(500);

        // Act: Testiamo il passaggio di null per staff e note
        int id = dao.insertPrenotazione(new Timestamp(System.currentTimeMillis()), 
                                        new Timestamp(System.currentTimeMillis()), 
                                        "In attesa", "2 persone", 1, 1, 1, null, null);

        // Assert
        assertEquals(500, id);
        // Verifichiamo che il DAO abbia chiamato setNull per i parametri opzionali
        verify(ps).setNull(8, Types.INTEGER); // IDStaff
        verify(ps).setNull(9, Types.VARCHAR); // NoteStaff
    }

    @Test
    void testSelectPrenotazione_EccezioneSQL() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Errore di rete"));

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            dao.selectPrenotazione(1);
        }, "Il DAO deve lanciare l'eccezione se il database fallisce");
    }
}