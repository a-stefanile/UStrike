package com.ustrike.model.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StaffUnitTest {

    @Test
    void testCostruttoreEGetter() {
        // Arrange & Act
        // Parametri: id, nome, cognome, email, passwordHash, ruolo
        Staff s = new Staff(1, "Marco", "Verdi", "marco.staff@ustrike.it", "hash_abc", "operatore");

        // Assert
        assertAll("Verifica campi Staff nel costruttore",
            () -> assertEquals(1, s.getIDStaff()),
            () -> assertEquals("Marco", s.getNomeStaff()),
            () -> assertEquals("Verdi", s.getCognomeStaff()),
            () -> assertEquals("marco.staff@ustrike.it", s.getEmail()),
            () -> assertEquals("hash_abc", s.getPasswordHash()),
            () -> assertEquals("operatore", s.getRuolo())
        );
    }

    @Test
    void testSetterEGetter() {
        // Arrange
        Staff s = new Staff();

        // Act
        s.setIDStaff(10);
        s.setNomeStaff("Anna");
        s.setCognomeStaff("Neri");
        s.setEmail("anna@ustrike.it");
        s.setPasswordHash("nuovo_hash_123");
        s.setRuolo("super-staff");

        // Assert
        assertAll("Verifica setter Staff",
            () -> assertEquals(10, s.getIDStaff()),
            () -> assertEquals("Anna", s.getNomeStaff()),
            () -> assertEquals("Neri", s.getCognomeStaff()),
            () -> assertEquals("anna@ustrike.it", s.getEmail()),
            () -> assertEquals("nuovo_hash_123", s.getPasswordHash()),
            () -> assertEquals("super-staff", s.getRuolo())
        );
    }

    @Test
    void testGetFullName() {
        // Arrange
        Staff s = new Staff();
        s.setNomeStaff("Alessandro");
        s.setCognomeStaff("Del Piero");

        // Act & Assert
        assertEquals("Alessandro Del Piero", s.getFullName(), "Il nome completo non corrisponde alla concatenazione attesa");
    }

    @Test
    void testCostruttoreVuoto() {
        // Act
        Staff s = new Staff();

        // Assert
        assertNotNull(s);
        assertEquals(0, s.getIDStaff());
        assertNull(s.getNomeStaff());
        assertNull(s.getRuolo());
    }
}