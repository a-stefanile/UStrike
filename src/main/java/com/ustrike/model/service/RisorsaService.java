package com.ustrike.model.service;

import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dto.Risorsa;

import java.util.List;

public class RisorsaService {

    private final RisorsaDAO dao = new RisorsaDAO();

    // ➕ AGGIUNTA RISORSA
    public void creaRisorsa(int stato, int capacita, int idServizio) {
        try {
            dao.insertRisorsa(stato, capacita, idServizio);
        } catch (Exception e) {
            throw new RuntimeException("Errore creazione risorsa", e);
        }
    }

    // 🗑️ RIMOZIONE RISORSA
    public boolean eliminaRisorsa(int idRisorsa) {
        try {
            return dao.deleteRisorsa(idRisorsa);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione risorsa", e);
        }
    }

    // ✏️ MODIFICA STATO + CAPACITÀ RISORSA
    public boolean aggiornaRisorsa(int idRisorsa, int nuovoStato, int nuovaCapacita) {
        try {
            return dao.updateRisorsa(idRisorsa, nuovoStato, nuovaCapacita);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento risorsa", e);
        }
    }

    // 🔄 SOLO STATO (es. abilita/disabilita risorsa)
    public boolean aggiornaStatoRisorsa(int idRisorsa, int nuovoStato) {
        try {
            Risorsa r = dao.selectRisorsa(idRisorsa);
            if (r == null) {
                throw new IllegalArgumentException("Risorsa inesistente");
            }
            return dao.updateRisorsa(idRisorsa, nuovoStato, r.getCapacita());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento stato risorsa", e);
        }
    }

    // 📋 TUTTE LE RISORSE
    public List<Risorsa> getTutteLeRisorse() {
        try {
            return dao.selectAllRisorse();
        } catch (Exception e) {
            throw new RuntimeException("Errore caricamento risorse", e);
        }
    }

    // 📋 RISORSE PER SERVIZIO (solo abilitate, ordinate per capacità)
    public List<Risorsa> getRisorseAbilitateByServizio(int idServizio) {
        try {
            return dao.selectRisorseByServizio(idServizio);
        } catch (Exception e) {
            throw new RuntimeException("Errore caricamento risorse per servizio", e);
        }
    }

    // 🔍 DETTAGLIO RISORSA
    public Risorsa getRisorsaById(int idRisorsa) {
        try {
            return dao.selectRisorsa(idRisorsa);
        } catch (Exception e) {
            throw new RuntimeException("Errore dettaglio risorsa", e);
        }
    }
}