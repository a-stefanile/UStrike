package com.ustrike.model.dao.IntegrationTestDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.ClienteDAO;
import com.ustrike.model.dto.Cliente;

class ClienteDAOIntegrationTest {

    private ClienteDAO dao;
    private int idDaCancellare = -1;
    private final String EMAIL_TEST = "integrazione_cliente@test.it";

    @BeforeEach
    void setUp() throws SQLException {
        dao = new ClienteDAO();
        // Pulizia preventiva: se il test precedente è fallito, rimuoviamo l'utente rimasto
        Cliente esistente = dao.selectClienteByEmail(EMAIL_TEST);
        if (esistente != null) {
            dao.deleteCliente(esistente.getIDCliente());
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Pulizia basata su ID per garantire che il DB resti immacolato
        if (idDaCancellare != -1) {
            dao.deleteCliente(idDaCancellare);
            idDaCancellare = -1;
        }
    }

    // --- GOOD PATH ---

    @Test
    void testLifecycleSuccesso() throws SQLException {
        // 1. Inserimento (INSERT)
        Cliente c = new Cliente(0, "Mario", "Rossi", EMAIL_TEST, "hash_pbkdf2_test", 0);
        assertTrue(dao.insertCliente(c), "L'inserimento del cliente deve riuscire");
        
        // Salviamo l'ID generato da MySQL
        idDaCancellare = c.getIDCliente();
        assertTrue(idDaCancellare > 0, "L'ID generato deve essere maggiore di 0");

        // 2. Lettura per Email (SELECT BY EMAIL)
        Cliente recuperato = dao.selectClienteByEmail(EMAIL_TEST);
        assertNotNull(recuperato);
        assertEquals("Mario", recuperato.getNomeCliente());

        // 3. Aggiornamento Punti (UPDATE)
        recuperato.setPuntiTicket(150);
        assertTrue(dao.updateCliente(recuperato), "L'aggiornamento dei punti deve riuscire");

        // 4. Verifica finale (SELECT BY ID)
        Cliente check = dao.selectClienteById(idDaCancellare);
        assertEquals(150, check.getPuntiTicket());
    }

    @Test
    void testUpdatePassword_Successo() throws SQLException {
        // Inserimento preliminare
        Cliente c = new Cliente(0, "Luca", "Verdi", EMAIL_TEST, "vecchia_password", 0);
        dao.insertCliente(c);
        idDaCancellare = c.getIDCliente();

        // Cambio password
        boolean updated = dao.updatePassword(idDaCancellare, "nuova_password_hashata");
        assertTrue(updated);

        Cliente check = dao.selectClienteById(idDaCancellare);
        assertEquals("nuova_password_hashata", check.getPasswordHash());
    }

    // --- BAD PATH ---

    @Test
    void testEmailDuplicata() throws SQLException {
        // Inseriamo il primo cliente
        Cliente c1 = new Cliente(0, "Primo", "Test", EMAIL_TEST, "pw1", 0);
        dao.insertCliente(c1);
        idDaCancellare = c1.getIDCliente();

        // Tentiamo di inserire un secondo cliente con la STESSA email
        Cliente c2 = new Cliente(0, "Secondo", "Test", EMAIL_TEST, "pw2", 0);
        
        // Il DB lancerà una SQLException per violazione del vincolo UNIQUE (uq_cliente_email)
        // Il DAO la lancia verso l'alto (throws SQLException)
        assertThrows(SQLException.class, () -> {
            dao.insertCliente(c2);
        }, "Il database deve impedire l'inserimento di email duplicate");
    }

    @Test
    void testNomeTroppoLungo() {
        // Nome di 60 caratteri (limite table è 52)
        String nomeLungo = "MarioMarioMarioMarioMarioMarioMarioMarioMarioMarioMarioMarioMario";
        Cliente c = new Cliente(0, nomeLungo, "Rossi", EMAIL_TEST, "pw", 0);

        // Dovrebbe lanciare SQLException (Data Truncation)
        assertThrows(SQLException.class, () -> {
            dao.insertCliente(c);
        });
    }

    @Test
    void testSelectInesistente() throws SQLException {
        Cliente c = dao.selectClienteById(999999);
        assertNull(c, "La ricerca di un ID inesistente deve restituire null");
    }
}