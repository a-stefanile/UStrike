package com.ustrike.model.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RisorsaUnitTest {

    @Test
    void testCostruttoreEGetter() {
        // Arrange & Act
        // Parametri: idRisorsa, stato, capacita, idServizio
        Risorsa r = new Risorsa(10, 1, 4, 2);

        // Assert
        assertAll("Verifica costruttore Risorsa",
            () -> assertEquals(10, r.getIDRisorsa()),
            () -> assertEquals(1, r.getStato()),
            () -> assertEquals(4, r.getCapacita()),
            () -> assertEquals(2, r.getIDServizio())
        );
    }

    @Test
    void testSetterEGetter() {
        // Arrange
        Risorsa r = new Risorsa();

        // Act
        r.setIDRisorsa(25);
        r.setStato(0);
        r.setCapacita(8);
        r.setIDServizio(5);

        // Assert
        assertEquals(25, r.getIDRisorsa());
        assertEquals(0, r.getStato());
        assertEquals(8, r.getCapacita());
        assertEquals(5, r.getIDServizio());
    }

    @Test
    void testToString() {
        // Arrange
        Risorsa r = new Risorsa(1, 1, 5, 10);
        
        // Act
        String result = r.toString();
        
        // Assert
        // Verifichiamo che la stringa contenga i valori chiave
        assertTrue(result.contains("IDRisorsa=1"));
        assertTrue(result.contains("Stato=1"));
        assertTrue(result.contains("Capacita=5"));
        assertTrue(result.contains("IDServizio=10"));
    }

    @Test
    void testCostruttoreVuoto() {
        // Act
        Risorsa r = new Risorsa();

        // Assert
        assertNotNull(r);
        assertEquals(0, r.getIDRisorsa());
        assertEquals(0, r.getIDServizio());
    }
}