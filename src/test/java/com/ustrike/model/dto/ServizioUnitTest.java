package com.ustrike.model.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ServizioUnitTest {

    @Test
    void testCostruttoreCompleto() {
        // Arrange & Act
        // Parametri: idServizio, nomeServizio, statoServizio
        Servizio s = new Servizio(1, "Bowling", true);

        // Assert
        assertAll("Verifica costruttore Servizio",
            () -> assertEquals(1, s.getIDServizio(), "L'ID non corrisponde"),
            () -> assertEquals("Bowling", s.getNomeServizio(), "Il nome non corrisponde"),
            () -> assertTrue(s.getStatoServizio(), "Lo stato dovrebbe essere true")
        );
    }

    @Test
    void testGetterSetter() {
        // Arrange
        Servizio s = new Servizio();

        // Act
        s.setIDServizio(5);
        s.setNomeServizio("Calcetto");
        s.setStatoServizio(false);

        // Assert
        assertEquals(5, s.getIDServizio());
        assertEquals("Calcetto", s.getNomeServizio());
        assertFalse(s.getStatoServizio(), "Lo stato dovrebbe essere false");
    }

    @Test
    void testCostruttoreVuoto() {
        // Act
        Servizio s = new Servizio();

        // Assert
        assertNotNull(s);
        assertEquals(0, s.getIDServizio());
        assertNull(s.getNomeServizio());
        assertFalse(s.getStatoServizio());
    }
}