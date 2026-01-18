package com.ustrike.model.service;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import com.ustrike.model.dto.PrenotazioneView;

import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

public class PrenotazioneService {
    private  PrenotazioneDAO dao;
    
    public PrenotazioneService() {
    	this.dao = new PrenotazioneDAO();
    }
    
    public PrenotazioneService(PrenotazioneDAO dao) {
    	this.dao = dao;
    }

    public List<Prenotazione> getCatalogoInAttesa() {
        try {
            return dao.selectPrenotazioniInAttesa();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo in attesa", e);
        }
    }

    public List<Prenotazione> getCatalogoCompletate() {
        try {
            return dao.selectPrenotazioniCompletate();
        } catch (Exception e) {
            throw new RuntimeException("Errore catalogo completate", e);
        }
    }

    public List<Prenotazione> getPrenotazioniCliente(int idCliente, HttpSession session) {
        String cacheKey = "prenotazioni_" + idCliente;

        if (session != null) {
            @SuppressWarnings("unchecked")
            List<Prenotazione> cached = (List<Prenotazione>) session.getAttribute(cacheKey);
            if (cached != null) return cached;
        }

        try {
            List<Prenotazione> result = dao.selectPrenotazioniByCliente(idCliente);
            if (session != null) session.setAttribute(cacheKey, result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Errore prenotazioni cliente", e);
        }
    }

    public int creaPrenotazione(Timestamp data, Timestamp orario, String partecipanti,
                                int idServizio, int idRisorsa, int idCliente, HttpSession session) {
        try {
            int id = dao.insertPrenotazione(data, orario, "In attesa", partecipanti,
                    idServizio, idRisorsa, idCliente, null);

            if (session != null) {
                session.removeAttribute("prenotazioni_" + idCliente);
                session.removeAttribute("prenotazioni_view_" + idCliente);
            }
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Errore creazione prenotazione", e);
        }
    }

    public boolean accettaPrenotazione(int idPrenotazione, int idStaff, HttpSession session) {
        try {
            boolean ok = dao.updateStatoPrenotazione(idPrenotazione, "Confermata", idStaff);

            if (ok && session != null) {
                Prenotazione p = dao.selectPrenotazione(idPrenotazione);
                if (p != null) {
                    session.removeAttribute("prenotazioni_" + p.getIDCliente());
                    session.removeAttribute("prenotazioni_view_" + p.getIDCliente());
                }

            }
            return ok;
        } catch (Exception e) {
            throw new RuntimeException("Errore accettazione", e);
        }
    }

    public boolean rifiutaPrenotazione(int idPrenotazione, int idStaff, HttpSession session) {
        try {
            boolean ok = dao.updateStatoPrenotazione(idPrenotazione, "Rifiutata", idStaff);

            if (ok && session != null) {
                Prenotazione p = dao.selectPrenotazione(idPrenotazione);
                if (p != null) {
                    session.removeAttribute("prenotazioni_" + p.getIDCliente());
                    session.removeAttribute("prenotazioni_view_" + p.getIDCliente());
                }

            }
            return ok;
        } catch (Exception e) {
            throw new RuntimeException("Errore rifiuto", e);
        }
    }
    
    public List<PrenotazioneView> getPrenotazioniClienteView(int idCliente, HttpSession session) {
        String cacheKey = "prenotazioni_view_" + idCliente;

        if (session != null) {
            @SuppressWarnings("unchecked")
            List<PrenotazioneView> cached = (List<PrenotazioneView>) session.getAttribute(cacheKey);
            if (cached != null) return cached;
        }

        try {
            List<PrenotazioneView> result = dao.selectPrenotazioniByClienteView(idCliente);
            if (session != null) session.setAttribute(cacheKey, result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Errore prenotazioni cliente (view)", e);
        }
    }
}
