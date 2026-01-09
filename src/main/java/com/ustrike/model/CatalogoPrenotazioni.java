package com.ustrike.model;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import java.util.List;

public class CatalogoPrenotazioni {
    private static CatalogoPrenotazioni instance;  // Singleton ODD
    private final PrenotazioneDAO dao = new PrenotazioneDAO();

    private CatalogoPrenotazioni() {}

    public static synchronized CatalogoPrenotazioni getInstance() {
        if (instance == null) instance = new CatalogoPrenotazioni();
        return instance;
    }

    // ODD REQUIRED
    public List<Prenotazione> getPrenotazioniInAttesa() {
        try {
            return dao.selectPrenotazioniInAttesa();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Prenotazione> getTuttePrenotazioni() {
        try {
            return dao.selectAllPrenotazioni();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        // No-op o future cache
    }
}
