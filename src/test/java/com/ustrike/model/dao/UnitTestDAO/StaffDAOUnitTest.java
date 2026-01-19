package com.ustrike.model.dao.UnitTestDAO;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Staff;
import com.ustrike.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

class StaffDAOUnitTest {

    private StaffDAO staffDAO;
    private MockedStatic<DBConnection> mockedDbConnection;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        staffDAO = new StaffDAO();

        // Mock del metodo statico
        mockedDbConnection = mockStatic(DBConnection.class);
        mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (mockedDbConnection != null) {
            mockedDbConnection.close();
        }
    }

    @Test
    void testDoRetrieveByEmail_Trovato() throws Exception {
        // Arrange
        String emailTest = "staff@ustrike.it";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        
        when(mockResultSet.getInt("IDStaff")).thenReturn(1);
        when(mockResultSet.getString("Email")).thenReturn(emailTest);
        when(mockResultSet.getString("Ruolo")).thenReturn("admin");

        // Act
        Staff result = staffDAO.doRetrieveByEmail(emailTest);

        // Assert
        assertNotNull(result);
        assertEquals(emailTest, result.getEmail());
        assertEquals("admin", result.getRuolo());
        verify(mockPreparedStatement).setString(1, emailTest);
    }

    @Test
    void testEmailExists_True() throws Exception {
        // Arrange
        String emailTest = "staff@ustrike.it";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Se c'è una riga, l'email esiste

        // Act
        boolean exists = staffDAO.emailExists(emailTest);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testDoSave_Successo() throws Exception {
        // Arrange
        Staff nuovoStaff = new Staff(0, "Luca", "Bianchi", "luca@test.it", "hash_pw", "operatore");
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(15);

        // Act
        boolean salvato = staffDAO.doSave(nuovoStaff);

        // Assert
        assertTrue(salvato);
        assertEquals(15, nuovoStaff.getIDStaff());
        verify(mockPreparedStatement).setString(5, "operatore");
    }

    @Test
    void testDoRetrieveByRuolo_ListaPiena() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false); // Due risultati

        // Act
        List<Staff> lista = staffDAO.doRetrieveByRuolo("operatore");

        // Assert
        assertEquals(2, lista.size());
        verify(mockPreparedStatement).setString(1, "operatore");
    }

    @Test
    void testUpdatePassword_Successo() throws Exception {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean updated = staffDAO.updatePassword(1, "new_hash");

        // Assert
        assertTrue(updated);
        verify(mockPreparedStatement).setString(1, "new_hash");
        verify(mockPreparedStatement).setInt(2, 1);
    }
}