package com.ustrike.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Servizio;

class ServizioServiceUnitTest {

    private ServizioService service;

    @Mock
    private ServizioDAO daoMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ServizioService(daoMock);
    }

    @Test
    void testGetServiziAbilitati_Successo() throws Exception {
        // Arrange
        List<Servizio> listaFinta = new ArrayList<>();
        listaFinta.add(new Servizio());
        when(daoMock.doRetrieveEnabled()).thenReturn(listaFinta);

        // Act
        List<Servizio> risultato = service.getServiziAbilitati();

        // Assert
        assertNotNull(risultato);
        assertEquals(1, risultato.size());
        verify(daoMock).doRetrieveEnabled();
    }

    @Test
    void testGetServizioByNome_Trovato() throws Exception {
        // Arrange
        String nome = "Bowling";
        Servizio sFinto = new Servizio();
        sFinto.setNomeServizio(nome);
        when(daoMock.doRetrieveByNome(nome)).thenReturn(sFinto);

        // Act
        Servizio risultato = service.getServizioByNome(nome);

        // Assert
        assertNotNull(risultato);
        assertEquals(nome, risultato.getNomeServizio());
    }

    @Test
    void testAbilitaServizio_Successo() throws Exception {
        // Arrange
        int id = 1;
        Servizio sFinto = new Servizio();
        when(daoMock.doRetrieveByKey(id)).thenReturn(sFinto);
        when(daoMock.abilitaServizio(id)).thenReturn(true);

        // Act
        boolean esito = service.abilitaServizio(id);

        // Assert
        assertTrue(esito);
        verify(daoMock).doRetrieveByKey(id);
        verify(daoMock).abilitaServizio(id);
    }

    @Test
    void testAbilitaServizio_Inesistente() throws Exception {
        // Arrange
        int id = 99;
        // Simuliamo che il servizio non esista nel DB
        when(daoMock.doRetrieveByKey(id)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.abilitaServizio(id);
        });


        verify(daoMock, never()).abilitaServizio(anyInt());
    }

    @Test
    void testDisabilitaServizio_ErroreGenerico() throws Exception {
        int id = 1;
        when(daoMock.doRetrieveByKey(id)).thenAnswer(invocation -> {
            throw new Exception("DB down");
        });

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.disabilitaServizio(id);
        });

        assertEquals("Errore disabilitazione servizio", ex.getMessage());
    }
}