package com.ustrike.model.dao.IntegrationTestDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ustrike.model.dao.StaffDAO;
import com.ustrike.model.dto.Staff;

class StaffDAOIntegrationTest {

    private StaffDAO dao;
    private int idDaCancellare = -1;
    private final String EMAIL_TEST = "staff.test@ustrike.it";

    @BeforeEach
    void setUp() throws SQLException {
        dao = new StaffDAO();
        // Pulizia preventiva per garantire l'isolamento del test
        Staff esistente = dao.doRetrieveByEmail(EMAIL_TEST);
        if (esistente != null) {
            dao.doDelete(esistente.getIDStaff());
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Pulizia finale basata su ID
        if (idDaCancellare != -1) {
            dao.doDelete(idDaCancellare);
            idDaCancellare = -1;
        }
    }

    // --- GOOD PATH ---

    @Test
    void testLifecycleStaff_Successo() throws SQLException {
        // 1. SAVE
        Staff s = new Staff();
        s.setNomeStaff("Luca");
        s.setCognomeStaff("Bianchi");
        s.setEmail(EMAIL_TEST);
        s.setPasswordHash("hash_staff_123");
        s.setRuolo("GoKart");

        assertTrue(dao.doSave(s), "Il salvataggio dello staff deve riuscire");
        idDaCancellare = s.getIDStaff();
        assertTrue(idDaCancellare > 0);

        // 2. RETRIEVE BY EMAIL
        Staff recuperato = dao.doRetrieveByEmail(EMAIL_TEST);
        assertNotNull(recuperato);
        assertEquals("GoKart", recuperato.getRuolo());

        // 3. UPDATE RUOLO
        recuperato.setRuolo("Bowling");
        assertTrue(dao.doUpdate(recuperato), "L'update del ruolo deve riuscire");

        // 4. VERIFY
        Staff check = dao.doRetrieveByKey(idDaCancellare);
        assertEquals("Bowling", check.getRuolo());
    }

    @Test
    void testEmailExists_Successo() throws SQLException {
        Staff s = new Staff();
        s.setNomeStaff("Test");
        s.setCognomeStaff("User");
        s.setEmail(EMAIL_TEST);
        s.setPasswordHash("pw");
        s.setRuolo("Bowling");
        
        dao.doSave(s);
        idDaCancellare = s.getIDStaff();

        assertTrue(dao.emailExists(EMAIL_TEST), "emailExists deve tornare true per un'email presente");
        assertFalse(dao.emailExists("non.esisto@test.it"), "emailExists deve tornare false per email assente");
    }

    // --- BAD PATH ---

    @Test
    void testEmailDuplicata() throws SQLException {
        // Primo inserimento
        Staff s1 = new Staff();
        s1.setNomeStaff("Staff1");
        s1.setCognomeStaff("Test");
        s1.setEmail(EMAIL_TEST);
        s1.setPasswordHash("pw1");
        s1.setRuolo("Bowling");
        dao.doSave(s1);
        idDaCancellare = s1.getIDStaff();

        // Secondo inserimento con stessa email
        Staff s2 = new Staff();
        s2.setNomeStaff("Staff2");
        s2.setCognomeStaff("Test");
        s2.setEmail(EMAIL_TEST);
        s2.setPasswordHash("pw2");
        s2.setRuolo("GoKart");

        assertThrows(SQLException.class, () -> {
            dao.doSave(s2);
        }, "Il DB deve bloccare l'email duplicata dello staff");
    }

    @Test
    void testRuoloInvalido() throws SQLException {
        // Lo schema ammette solo 'Bowling' o 'GoKart'
        Staff s = new Staff();
        s.setNomeStaff("Error");
        s.setCognomeStaff("User");
        s.setEmail(EMAIL_TEST);
        s.setPasswordHash("pw");
        s.setRuolo("Manager"); 
        assertThrows(SQLException.class, () -> {
            dao.doSave(s);
        }, "L'inserimento di un ruolo non previsto dall'ENUM dovrebbe fallire");
    }

    @Test
    void testDeleteInesistente() throws SQLException {
        // ID che non esiste nel DB
        boolean result = dao.doDelete(999999);
        assertFalse(result, "La cancellazione di un ID inesistente deve tornare false");
    }
}