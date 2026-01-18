package com.ustrike.model.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;

class PrenotazioneUnitTest {

    @Test
    void testCostruttoreEGetter() {
        // Arrange
        long now = System.currentTimeMillis();
        Timestamp data = new Timestamp(now);
        Timestamp orario = new Timestamp(now);
        
        
        Prenotazione p = new Prenotazione(1, data, orario, "Confermata", "4 persone", 101, 201, 301, 5, "Nota di test");

        // Assert
        assertEquals(1, p.getIDPrenotazione());
        assertEquals(data, p.getData());
        assertEquals(orario, p.getOrario());
        assertEquals("Confermata", p.getStatoPrenotazione());
        assertEquals("4 persone", p.getPartecipanti());
        assertEquals(101, p.getIDServizio());
        assertEquals(201, p.getIDRisorsa());
        assertEquals(301, p.getIDCliente());
        assertEquals(5, p.getIDStaff());
        assertEquals("Nota di test", p.getNoteStaff());
    }

    @Test
    void testSetterEGetter() {
        // Arrange
        Prenotazione p = new Prenotazione();
        Timestamp data = Timestamp.valueOf("2026-05-20 00:00:00");
        Timestamp orario = Timestamp.valueOf("2026-05-20 21:00:00");

        // Act
        p.setIDPrenotazione(100);
        p.setData(data);
        p.setOrario(orario);
        p.setStatoPrenotazione("In Attesa");
        p.setPartecipanti("2 persone");
        p.setIDServizio(1);
        p.setIDRisorsa(2);
        p.setIDCliente(3);
        p.setIDStaff(null);
        p.setNoteStaff("Nessuna nota");

        // Assert
        assertAll("Verifica tutti i setter di Prenotazione",
            () -> assertEquals(100, p.getIDPrenotazione()),
            () -> assertEquals(data, p.getData()),
            () -> assertEquals(orario, p.getOrario()),
            () -> assertEquals("In Attesa", p.getStatoPrenotazione()),
            () -> assertEquals("2 persone", p.getPartecipanti()),
            () -> assertNull(p.getIDStaff()),
            () -> assertEquals("Nessuna nota", p.getNoteStaff())
        );
    }

    @Test
    void testCostruttoreVuoto() {
        Prenotazione p = new Prenotazione();
        assertNotNull(p);
        assertEquals(0, p.getIDPrenotazione());
        assertNull(p.getStatoPrenotazione());
        assertNull(p.getNoteStaff());
    }
}