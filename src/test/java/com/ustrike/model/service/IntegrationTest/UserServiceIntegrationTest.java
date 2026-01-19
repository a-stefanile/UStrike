package com.ustrike.model.service.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Cliente;
import com.ustrike.model.dto.Staff;
import com.ustrike.model.service.UserService;

class UserServiceIntegrationTest {

    private UserService userService;
    private ClienteDAO clienteDAO;
    private StaffDAO staffDAO;

    // Costanti per evitare collisioni nel DB reale
    private final String EMAIL_CLIENTE = "mario.rossi@integration.it";
    private final String EMAIL_STAFF = "anna.staff@integration.it";

    @BeforeEach
    void setUp() throws SQLException {
        clienteDAO = new ClienteDAO();
        staffDAO = new StaffDAO();
        userService = new UserService(clienteDAO, staffDAO);

        // Pulizia preventiva: eliminiamo eventuali residui di test precedenti
        cleanup();
    }

    @AfterEach
    void tearDown() throws SQLException {
        cleanup();
    }

    private void cleanup() throws SQLException {
        Cliente c = clienteDAO.selectClienteByEmail(EMAIL_CLIENTE);
        if (c != null) clienteDAO.deleteCliente(c.getIDCliente());

        Staff s = staffDAO.doRetrieveByEmail(EMAIL_STAFF);
        if (s != null) staffDAO.doDelete(s.getIDStaff());
    }

    // --- GOOD PATHS ---

    @Test
    void testRegistrazioneELoginCliente_Successo() throws SQLException {
        // 1. Registrazione tramite Service (UC1)
        Cliente c = new Cliente(0, "Mario", "Rossi", EMAIL_CLIENTE, null, 0);
        String passChiaro = "PasswordSicura123!";
        
        boolean creato = userService.createCliente(c, passChiaro);
        assertTrue(creato, "La registrazione del cliente deve riuscire");
        assertTrue(c.getIDCliente() > 0, "L'ID generato deve essere valorizzato nel DTO");

        // 2. Login Unificato (UC2)
        Object[] auth = (Object[]) userService.authenticateUser(EMAIL_CLIENTE, passChiaro);
        
        assertNotNull(auth, "L'autenticazione deve avere successo");
        assertEquals("cliente", auth[0]);
        assertEquals(c.getIDCliente(), auth[1]);
        assertEquals("Mario", auth[2]);
    }

    @Test
    void testLoginStaff_Successo() throws SQLException {
        // Creazione Staff (UC21)
        Staff s = new Staff();
        s.setNomeStaff("Anna");
        s.setCognomeStaff("Verdi");
        s.setEmail(EMAIL_STAFF);
        s.setRuolo("Bowling");
        
        boolean creato = userService.createStaff(s, "StaffSecret2026");
        assertTrue(creato);

        // Login Unificato
        Object[] auth = (Object[]) userService.authenticateUser(EMAIL_STAFF, "StaffSecret2026");
        
        assertNotNull(auth);
        assertEquals("staff", auth[0]);
        assertEquals("Anna", auth[2]);
    }

    @Test
    void testCambioPassword_Successo() throws SQLException {
        // Setup: registriamo un utente
        Cliente c = new Cliente(0, "User", "Test", EMAIL_CLIENTE, null, 0);
        userService.createCliente(c, "vecchiaPass");

        // Act: cambio password tramite service
        boolean ok = userService.changePassword(EMAIL_CLIENTE, "cliente", "vecchiaPass", "nuovaPass");
        assertTrue(ok, "Il cambio password dovrebbe riuscire");

        // Assert: login con nuova password funziona, con vecchia fallisce
        assertNotNull(userService.authenticateUser(EMAIL_CLIENTE, "nuovaPass"));
        assertNull(userService.authenticateUser(EMAIL_CLIENTE, "vecchiaPass"));
    }

    // --- BAD PATHS ---

    @Test
    void testRegistrazioneEmailDuplicata_DeveFallire() throws SQLException {
        Cliente c1 = new Cliente(0, "Primo", "User", EMAIL_CLIENTE, null, 0);
        userService.createCliente(c1, "pass1");

        Cliente c2 = new Cliente(0, "Secondo", "User", EMAIL_CLIENTE, null, 0);
        
        // Il service deve intercettare l'email esistente tramite selectClienteByEmail e tornare false
        boolean creato = userService.createCliente(c2, "pass2");
        assertFalse(creato, "Il sistema non deve permettere email duplicate");
    }

    @Test
    void testLogin_EmailInesistente() throws SQLException {
        Object auth = userService.authenticateUser("non.esisto@fail.com", "anyPass");
        assertNull(auth, "Il login deve fallire se l'email non è in nessuna tabella");
    }

    @Test
    void testUpdateUser_Successo() throws SQLException {
        // Setup
        Cliente c = new Cliente(0, "Originale", "User", EMAIL_CLIENTE, null, 0);
        userService.createCliente(c, "pass");
        
        // Modifica dati (non password)
        c.setNomeCliente("Modificato");
        c.setPuntiTicket(500);
        
        boolean updateOk = userService.updateUser(c, "cliente");
        assertTrue(updateOk);

        // Verifica su DB
        Cliente verificato = clienteDAO.selectClienteById(c.getIDCliente());
        assertEquals("Modificato", verificato.getNomeCliente());
        assertEquals(500, verificato.getPuntiTicket());
    }
}