package com.ustrike.model.service;

import com.ustrike.model.dao.ServizioDAO;
import com.ustrike.model.dto.Servizio;

import java.util.List;

public class ServizioService {

	private ServizioDAO dao; 

    // Costruttore standard per l'app
    public ServizioService() {
        this.dao = new ServizioDAO();
    }

    // Costruttore per il test
    public ServizioService(ServizioDAO dao) {
        this.dao = dao;
    }

    // ðŸ“‹ CLIENTE: servizi abilitati
    public List<Servizio> getServiziAbilitati() {
        try {
            return dao.doRetrieveEnabled();
        } catch (Exception e) {
            throw new RuntimeException("Errore servizi abilitati", e);
        }
    }

    // ðŸ“‹ STAFF/MANAGER: tutti i servizi
    public List<Servizio> getTuttiIServizi() {
        try {
            return dao.doRetrieveAll();
        } catch (Exception e) {
            throw new RuntimeException("Errore caricamento servizi", e);
        }
    }

    // ðŸ”� DETTAGLIO PER ID
    public Servizio getServizioById(int idServizio) {
        try {
            return dao.doRetrieveByKey(idServizio);
        } catch (Exception e) {
            throw new RuntimeException("Errore dettaglio servizio", e);
        }
    }

    // ðŸ”� DETTAGLIO PER NOME (Bowling / Go-Kart / Biliardo)
    public Servizio getServizioByNome(String nomeServizio) {
        try {
            return dao.doRetrieveByNome(nomeServizio);
        } catch (Exception e) {
            throw new RuntimeException("Errore ricerca servizio per nome", e);
        }
    }

    // âœ… STAFF: abilita servizio
    public boolean abilitaServizio(int idServizio) {
        try {
            Servizio s = dao.doRetrieveByKey(idServizio);
            if (s == null) {
                throw new IllegalArgumentException("Servizio inesistente");
            }
            return dao.abilitaServizio(idServizio);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore abilitazione servizio", e);
        }
    }

    // â�Œ STAFF: disabilita servizio
    public boolean disabilitaServizio(int idServizio) {
        try {
            Servizio s = dao.doRetrieveByKey(idServizio);
            if (s == null) {
                throw new IllegalArgumentException("Servizio inesistente");
            }
            return dao.disabilitaServizio(idServizio);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore disabilitazione servizio", e);
        }
    }
}
