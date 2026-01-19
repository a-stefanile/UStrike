package com.ustrike.model.dao.UnitTestDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dto.Cliente;
import com.ustrike.util.DBConnection;

class ClienteDAOUnitTest {

    private ClienteDAO clienteDAO;
    private MockedStatic<DBConnection> mockedDbConnection;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        clienteDAO = new ClienteDAO();

        // Mockiamo il metodo statico getConnection()
        mockedDbConnection = mockStatic(DBConnection.class);
        mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        // Fondamentale: chiudere il mock statico dopo ogni test
        mockedDbConnection.close();
    }

    @Test
    void testSelectClienteById_Success() throws Exception {
        // Arrange
        int idTest = 1;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        // Simuliamo il ResultSet
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("IDCliente")).thenReturn(idTest);
        when(mockResultSet.getString("NomeCliente")).thenReturn("Mario");
        when(mockResultSet.getString("CognomeCliente")).thenReturn("Rossi");
        when(mockResultSet.getString("Email")).thenReturn("mario@test.com");
        when(mockResultSet.getString("PasswordHash")).thenReturn("hash123");
        when(mockResultSet.getInt("PuntiTicket")).thenReturn(100);

        // Act
        Cliente result = clienteDAO.selectClienteById(idTest);

        // Assert
        assertNotNull(result);
        assertEquals("Mario", result.getNomeCliente());
        verify(mockPreparedStatement).setInt(1, idTest);
    }

    @Test
    void testInsertCliente_Success() throws Exception {
        // Arrange
        Cliente nuovoCliente = new Cliente(0, "Anna", "Verdi", "anna@test.com", "pw", 50);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Simuliamo il recupero della chiave generata (IDCliente)
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(99);

        // Act
        boolean inserito = clienteDAO.insertCliente(nuovoCliente);

        // Assert
        assertTrue(inserito);
        assertEquals(99, nuovoCliente.getIDCliente());
        verify(mockPreparedStatement).setString(1, "Anna");
    }

    @Test
    void testDeleteCliente_Success() throws Exception {
        // Arrange
        int idDaEliminare = 10;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        boolean eliminato = clienteDAO.deleteCliente(idDaEliminare);

        // Assert
        assertTrue(eliminato);
        verify(mockPreparedStatement).setInt(1, idDaEliminare);
    }
    
}