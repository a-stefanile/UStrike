package com.ustrike.model.dao.UnitTestDAO;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dto.Risorsa;
import com.ustrike.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

class RisorsaDAOUnitTest {

    private RisorsaDAO dao;
    private MockedStatic<DBConnection> mockedDb;

    @Mock private Connection conn;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dao = new RisorsaDAO();
        mockedDb = mockStatic(DBConnection.class);
        mockedDb.when(DBConnection::getConnection).thenReturn(conn);
    }

    @AfterEach
    void tearDown() {
        mockedDb.close();
    }

    @Test
    void testInsertRisorsa_Successo() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(77);

        // Act
        int generatedId = dao.insertRisorsa(1, 4, 10);

        // Assert
        assertEquals(77, generatedId);
        verify(ps).setInt(1, 1); // Stato
        verify(ps).setInt(2, 4); // Capacità
        verify(ps).setInt(3, 10); // IDServizio
    }

    @Test
    void testIsDisponibile_RisorsaLibera() throws Exception {
        // Arrange
        Timestamp orarioTest = Timestamp.valueOf("2026-05-20 18:00:00");
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        
        // Simuliamo che il COUNT(*) restituisca 0 (nessuna prenotazione esistente)
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(0);

        // Act
        boolean disponibile = dao.isDisponibile(1, orarioTest);

        // Assert
        assertTrue(disponibile, "La risorsa dovrebbe essere disponibile se il conteggio è 0");
    }

    @Test
    void testIsDisponibile_RisorsaOccupata() throws Exception {
        // Arrange
        Timestamp orarioTest = Timestamp.valueOf("2026-05-20 18:00:00");
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        
        // Simuliamo che il COUNT(*) restituisca 1 (risorsa già occupata)
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        // Act
        boolean disponibile = dao.isDisponibile(1, orarioTest);

        // Assert
        assertFalse(disponibile, "La risorsa NON dovrebbe essere disponibile se il conteggio è > 0");
    }

    @Test
    void testDeleteRisorsa_Inesistente() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0); // Nessuna riga eliminata

        // Act
        boolean result = dao.deleteRisorsa(999);

        // Assert
        assertFalse(result, "Dovrebbe restituire false se provo a eliminare un ID che non esiste");
    }

    @Test
    void testSelectRisorseByServizio_ListaVuota() throws Exception {
        // Arrange
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // Nessun risultato per quel servizio

        // Act
        List<Risorsa> list = dao.selectRisorseByServizio(5);

        // Assert
        assertNotNull(list);
        assertTrue(list.isEmpty(), "Dovrebbe restituire una lista vuota, non null");
    }
}