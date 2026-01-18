package com.ustrike.model.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;

class PrenotazioneViewUnitTest {

    @Test
    void testGetterSetterAggregati() {
        // Arrange
        PrenotazioneView view = new PrenotazioneView();
        Timestamp data = Timestamp.valueOf("2026-06-15 00:00:00");
        Timestamp orario = Timestamp.valueOf("2026-06-15 19:00:00");

        // Act
        view.setIDPrenotazione(50);
        view.setData(data);
        view.setOrario(orario);
        view.setStatoPrenotazione("Confermata");
        view.setPartecipanti("5 Persone");
        
        // Dati aggregati (quelli che vengono da altre tabelle nel DB)
        view.setIDServizio(1);
        view.setNomeServizio("Bowling");
        view.setIDRisorsa(10);
        view.setCapacitaRisorsa(6);
        view.setIDStaff(3);

        // Assert
        assertAll("Verifica integrità dati PrenotazioneView",
            () -> assertEquals(50, view.getIDPrenotazione()),
            () -> assertEquals("Bowling", view.getNomeServizio(), "Il nome del servizio non corrisponde"),
            () -> assertEquals(6, view.getCapacitaRisorsa(), "La capacità della risorsa non corrisponde"),
            () -> assertEquals(3, view.getIDStaff(), "L'ID dello staff non è corretto"),
            () -> assertEquals(orario, view.getOrario())
        );
    }

    @Test
    void testGestioneStaffNull() {
        // Arrange & Act
        PrenotazioneView view = new PrenotazioneView();
        view.setIDStaff(null); // Caso prenotazione ancora da gestire

        // Assert
        assertNull(view.getIDStaff(), "L'IDStaff dovrebbe poter essere null");
    }

    @Test
    void testValoriDefault() {
        // Arrange & Act
        PrenotazioneView view = new PrenotazioneView();

        // Assert
        assertEquals(0, view.getIDPrenotazione());
        assertEquals(0, view.getCapacitaRisorsa());
        assertNull(view.getNomeServizio());
        assertNull(view.getData());
    }
}