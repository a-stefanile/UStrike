package com.ustrike.model.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ManagerUnitTest {

    @Test
    void testCostruttoreCompleto() {
        // Arrange & Act
        // Creiamo un'istanza usando il costruttore con tutti i parametri
        Manager m = new Manager(1, "Mario", "Rossi", "mario@test.it", "hash123", "manager");

        // Assert
        assertEquals(1, m.getIDManager(), "L'ID dovrebbe essere 1");
        assertEquals("Mario", m.getNomeManager(), "Il nome dovrebbe essere Mario");
        assertEquals("Rossi", m.getCognomeManager(), "Il cognome dovrebbe essere Rossi");
        assertEquals("mario@test.it", m.getEmail(), "L'email dovrebbe essere corretta");
        assertEquals("hash123", m.getPasswordHash(), "L'hash password dovrebbe corrispondere");
        assertEquals("manager", m.getRuoloManager(), "Il ruolo dovrebbe essere manager");
    }

    @Test
    void testGetterSetter() {
        // Arrange
        Manager m = new Manager();

        // Act
        m.setIDManager(5);
        m.setNomeManager("Elena");
        m.setCognomeManager("Gialli");
        m.setEmail("elena@test.it");
        m.setPasswordHash("secret");
        m.setRuoloManager("staff");

        // Assert
        assertEquals(5, m.getIDManager());
        assertEquals("Elena", m.getNomeManager());
        assertEquals("Gialli", m.getCognomeManager());
        assertEquals("elena@test.it", m.getEmail());
        assertEquals("secret", m.getPasswordHash());
        assertEquals("staff", m.getRuoloManager());
    }

    @Test
    void testGetFullName() {
        // Arrange
        Manager m = new Manager();
        m.setNomeManager("Luca");
        m.setCognomeManager("Verdi");

        // Act & Assert
        // Testiamo il metodo di utilità che concatena nome e cognome
        assertEquals("Luca Verdi", m.getFullName(), "Il nome completo non è concatenato correttamente");
    }

    @Test
    void testCostruttoreVuoto() {
        // Arrange & Act
        Manager m = new Manager();

        // Assert
        assertNotNull(m, "L'oggetto non dovrebbe essere null");
        assertEquals(0, m.getIDManager(), "L'ID di default dovrebbe essere 0");
        assertNull(m.getNomeManager(), "Il nome di default dovrebbe essere null");
    }
}