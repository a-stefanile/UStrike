package com.ustrike.model;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import java.util.List;

public class CatalogoPrenotazioni {
    private static CatalogoPrenotazioni instance;
    private final PrenotazioneDAO dao = new PrenotazioneDAO();

    private CatalogoPrenotazioni() {}

    public static synchronized CatalogoPrenotazioni getInstance() {
        if (instance == null) instance = new CatalogoPrenotazioni();
        return instance;
    }

    public List<Prenotazione> getPrenotazioniInAttesa() {
        try {
            return dao.selectPrenotazioniInAttesa();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo prenotazioni in attesa", e);
        }
    }

    public List<Prenotazione> getPrenotazioniCompletate() {
        try {
            return dao.selectPrenotazioniCompletate();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo prenotazioni completate", e);
        }
    }

    // “get all”
    public List<Prenotazione> getTuttePrenotazioni() {
        try {
            return dao.selectAllPrenotazioni();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo tutte prenotazioni", e);
        }
    }

    // No cache in memoria -> no-op
    public void refresh() {
        // intentionally empty
    }
}
