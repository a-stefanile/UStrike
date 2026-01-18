package com.ustrike.model.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Cliente;
import com.ustrike.model.dto.Staff;
import com.ustrike.util.PasswordHasher;

class UserServiceUnitTest {

    private UserService userService;

    @Mock
    private ClienteDAO clienteDAOMock;

    @Mock
    private StaffDAO staffDAOMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(clienteDAOMock, staffDAOMock);
    }

    @Test
    void testAuthenticateUser_ClienteSuccesso() throws Exception {
        // Arrange
        String email = "test@cliente.it";
        String password = "password123";
        String hash = PasswordHasher.hash(password);

        Cliente cFinto = new Cliente();
        cFinto.setIDCliente(1);
        cFinto.setEmail(email);
        cFinto.setNomeCliente("Mario");
        cFinto.setPasswordHash(hash);

        when(clienteDAOMock.selectClienteByEmail(email)).thenReturn(cFinto);

        // Act
        Object[] result = (Object[]) userService.authenticateUser(email, password);

        // Assert
        assertNotNull(result);
        assertEquals("cliente", result[0]);
        assertEquals(1, result[1]);
        assertEquals("Mario", result[2]);
    }

    @Test
    void testAuthenticateUser_LoginFallito() throws Exception {
        // Arrange
        String email = "wrong@user.it";
        when(clienteDAOMock.selectClienteByEmail(email)).thenReturn(null);
        when(staffDAOMock.doRetrieveByEmail(email)).thenReturn(null);

        // Act
        Object result = userService.authenticateUser(email, "anyPassword");

        // Assert
        assertNull(result);
    }

    @Test
    void testCreateCliente_EmailGiaEsistente() throws Exception {

        Cliente nuovoCliente = new Cliente();
        nuovoCliente.setEmail("esiste@test.it");

        when(clienteDAOMock.selectClienteByEmail("esiste@test.it")).thenReturn(new Cliente());

        boolean creato = userService.createCliente(nuovoCliente, "password");


        assertFalse(creato, "Non dovrebbe permettere la registrazione se l'email esiste");
        verify(clienteDAOMock, never()).insertCliente(any());
    }

    @Test
    void testUpdateUser_StaffSuccesso() throws Exception {
        Staff s = new Staff();
        when(staffDAOMock.doUpdate(s)).thenReturn(true);

        boolean esito = userService.updateUser(s, "staff");

        assertTrue(esito);
        verify(staffDAOMock).doUpdate(s);
    }
}