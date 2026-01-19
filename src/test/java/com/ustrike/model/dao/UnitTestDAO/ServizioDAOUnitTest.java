package com.ustrike.model.dao.UnitTestDAO;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Servizio;
import com.ustrike.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

class ServizioDAOUnitTest {

    private ServizioDAO dao;
    private MockedStatic<DBConnection> mockedDb;

    @Mock private Connection conn;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dao = new ServizioDAO();
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    void tearDown() {
        mockedDb.close();
    }

    // --- TEST POSITIVI (Happy Path) ---

    @Test
    void testDoSave_Successo() throws Exception {
        Servizio s = new Servizio(0, "Bowling", true);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        boolean result = dao.doSave(s);

        assertTrue(result);
        verify(ps).setString(1, "Bowling");
        verify(ps).setBoolean(2, true);
    }

    @Test
    void testDoRetrieveByKey_Trovato() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("IDServizio")).thenReturn(1);
        when(rs.getString("NomeServizio")).thenReturn("Calcetto");
        when(rs.getBoolean("StatoServizio")).thenReturn(true);

        Servizio result = dao.doRetrieveByKey(1);

        assertNotNull(result);
        assertEquals("Calcetto", result.getNomeServizio());
        assertTrue(result.getStatoServizio());
    }

    @Test
    void testDisabilitaServizio_Successo() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        boolean result = dao.disabilitaServizio(1);

        assertTrue(result);
        verify(ps).setInt(1, 1);
    }

    // --- TEST NEGATIVI ED ERRORI ---

    @Test
    void testDoRetrieveByKey_NonTrovato() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // Nessuna riga trovata

        Servizio result = dao.doRetrieveByKey(999);

        assertNull(result, "Dovrebbe restituire null se la chiave non esiste");
    }

    @Test
    void testDoSave_ErroreSQL() throws Exception {
        // Simuliamo una SQLException durante l'esecuzione
        Servizio s = new Servizio(0, "Error", true);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Errore di database"));

        boolean result = dao.doSave(s);

        // Poiché il tuo DAO cattura l'eccezione e ritorna false
        assertFalse(result, "Dovrebbe restituire false in caso di SQLException");
    }

    @Test
    void testDoRetrieveAll_ListaVuota() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<Servizio> lista = dao.doRetrieveAll();

        assertNotNull(lista);
        assertTrue(lista.isEmpty(), "Dovrebbe restituire una lista vuota e non null se il DB è vuoto");
    }
}