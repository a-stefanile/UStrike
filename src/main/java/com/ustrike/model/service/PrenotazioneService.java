package com.ustrike.model.service;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

public class PrenotazioneService {
    private final PrenotazioneDAO dao = new PrenotazioneDAO();

    // CATALOGO STAFF (In Attesa)
    public List<Prenotazione> getCatalogoInAttesa() {
        try {
            return dao.selectPrenotazioniInAttesa();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo", e);
        }
    }

    //  CLIENTE (cache + DAO)
    public List<Prenotazione> getPrenotazioniCliente(int idCliente, HttpSession session) {
        if (session != null) {
            String cacheKey = "prenotazioni_" + idCliente;
            @SuppressWarnings("unchecked")
            List<Prenotazione> cached = (List<Prenotazione>) session.getAttribute(cacheKey);
            if (cached != null) return cached;
        }
        
        try {
            List<Prenotazione> result = dao.selectPrenotazioniByCliente(idCliente);
            if (session != null) {
                session.setAttribute("prenotazioni_" + idCliente, result);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Errore prenotazioni cliente", e);
        }
    }

    // ➕ CREA PRENOTAZIONE
    public void creaPrenotazione(Timestamp data, Timestamp orario, String partecipanti, 
                                int idServizio, int idRisorsa, int idCliente) {
        try {
            dao.insertPrenotazione(data, orario, "In attesa", partecipanti, 
                                 idServizio, idRisorsa, idCliente, null);
        } catch (Exception e) {
            throw new RuntimeException("Errore creazione", e);
        }
    }

    // STAFF ACCETTA
    public boolean accettaPrenotazione(int idPrenotazione, int idStaff) {
        try {
            return dao.updateStatoPrenotazione(idPrenotazione, "Confermata", idStaff);
        } catch (Exception e) {
            throw new RuntimeException("Errore accettazione", e);
        }
    }

    // STAFF RIFIUTA
    public boolean rifiutaPrenotazione(int idPrenotazione, int idStaff, String motivo) {
        try {
            return dao.updateStatoPrenotazione(idPrenotazione, "Rifiutata", idStaff);
        } catch (Exception e) {
            throw new RuntimeException("Errore rifiuto", e);
        }
    }
    
    public List<Prenotazione> getCatalogoCompletate() {
        try {
            return dao.selectPrenotazioniCompletate();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo completate", e);
        }
    }

    // TEST STANDALONE (senza session/singleton)
    public List<Prenotazione> getCatalogoTest() throws Exception {
        return dao.selectPrenotazioniInAttesa();
    }
}
