package com.ustrike.model.service.UnitTest;

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
import com.ustrike.model.service.UserService;
import com.ustrike.util.PasswordHasher;

class UserServiceUnitTest {

    private UserService userService;

    @Mock private ClienteDAO clienteDAOMock;
    @Mock private StaffDAO staffDAOMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(clienteDAOMock, staffDAOMock);
    }

    // --- TEST AUTHENTICATE (LOGIN) ---

    @Test
    void testAuthenticateUser_ClienteSuccesso() throws Exception {
        String email = "mario@test.it";
        String password = "password123";
        String hash = PasswordHasher.hash(password);

        Cliente c = new Cliente();
        c.setIDCliente(10);
        c.setNomeCliente("Mario");
        c.setPasswordHash(hash);

        when(clienteDAOMock.selectClienteByEmail(email)).thenReturn(c);

        Object[] result = (Object[]) userService.authenticateUser(email, password);

        assertNotNull(result);
        assertEquals("cliente", result[0]);
        assertEquals(10, result[1]);
        assertEquals("Mario", result[2]);
    }

    @Test
    void testAuthenticateUser_StaffSuccesso() throws Exception {
        String email = "staff@ustrike.it";
        String password = "staffPassword";
        String hash = PasswordHasher.hash(password);

        Staff s = new Staff();
        s.setIDStaff(20);
        s.setNomeStaff("Anna");
        s.setPasswordHash(hash);

        // Simuliamo che non sia un cliente ma sia uno staff
        when(clienteDAOMock.selectClienteByEmail(email)).thenReturn(null);
        when(staffDAOMock.doRetrieveByEmail(email)).thenReturn(s);

        Object[] result = (Object[]) userService.authenticateUser(email, password);

        assertNotNull(result);
        assertEquals("staff", result[0]);
        assertEquals(20, result[1]);
    }

    // --- TEST REGISTRAZIONE ---

    @Test
    void testCreateCliente_Successo_ConHash() throws Exception {
        Cliente nuovo = new Cliente();
        nuovo.setEmail("nuovo@test.it");
        String passPlain = "secret123";

        when(clienteDAOMock.selectClienteByEmail(anyString())).thenReturn(null);
        when(clienteDAOMock.insertCliente(any(Cliente.class))).thenReturn(true);

        boolean esito = userService.createCliente(nuovo, passPlain);

        assertTrue(esito);
        // Verifica che la password salvata nel DTO sia stata hashata (non è più "secret123")
        assertNotEquals(passPlain, nuovo.getPasswordHash());
        assertTrue(PasswordHasher.verify(passPlain, nuovo.getPasswordHash()));
        verify(clienteDAOMock).insertCliente(nuovo);
    }

    // --- TEST CHANGE PASSWORD ---

    @Test
    void testChangePassword_Successo() throws Exception {
        String email = "mario@test.it";
        String vecchia = "vecchiaPass";
        String nuova = "nuovaPass";
        String hashVecchio = PasswordHasher.hash(vecchia);

        Cliente c = new Cliente();
        c.setIDCliente(10);
        c.setPasswordHash(hashVecchio);

        when(clienteDAOMock.selectClienteByEmail(email)).thenReturn(c);
        when(clienteDAOMock.updatePassword(eq(10), anyString())).thenReturn(true);

        boolean esito = userService.changePassword(email, "cliente", vecchia, nuova);

        assertTrue(esito);
        // Verifica che il DAO riceva un nuovo hash diverso dal vecchio
        verify(clienteDAOMock).updatePassword(eq(10), argThat(h -> !h.equals(hashVecchio)));
    }

    @Test
    void testChangePassword_VecchiaErrata_Fallimento() throws Exception {
        String email = "mario@test.it";
        Cliente c = new Cliente();
        c.setPasswordHash(PasswordHasher.hash("giusta"));

        when(clienteDAOMock.selectClienteByEmail(email)).thenReturn(c);

        // Tentativo con password "sbagliata"
        boolean esito = userService.changePassword(email, "cliente", "sbagliata", "nuova");

        assertFalse(esito);
        verify(clienteDAOMock, never()).updatePassword(anyInt(), anyString());
    }
}