package com.ustrike.model.service;

import com.ustrike.model.dao.RisorsaDAO;
import com.ustrike.model.dto.Risorsa;
import java.sql.Timestamp;
import java.util.List;

public class RisorsaService {

	private RisorsaDAO dao;

    // 2. Costruttore standard
    public RisorsaService() {
        this.dao = new RisorsaDAO();
    }

    // 3. Costruttore per i test
    public RisorsaService(RisorsaDAO dao) {
        this.dao = dao;
    }

    public int creaRisorsa(int stato, int capacita, int idServizio) {
        try {
            return dao.insertRisorsa(stato, capacita, idServizio);
        } catch (Exception e) {
            throw new RuntimeException("Errore creazione risorsa", e);
        }
    }

    public boolean eliminaRisorsa(int idRisorsa) {
        try {
            return dao.deleteRisorsa(idRisorsa);
        } catch (Exception e) {
            throw new RuntimeException("Errore eliminazione risorsa", e);
        }
    }

    public boolean aggiornaRisorsa(int idRisorsa, int nuovoStato, int nuovaCapacita) {
        try {
            return dao.updateRisorsa(idRisorsa, nuovoStato, nuovaCapacita);
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento risorsa", e);
        }
    }

    public boolean aggiornaStatoRisorsa(int idRisorsa, int nuovoStato) {
        try {
            Risorsa r = dao.selectRisorsa(idRisorsa);
            if (r == null) {
                throw new IllegalArgumentException("Risorsa ID " + idRisorsa + " inesistente");
            }
            return dao.updateRisorsa(idRisorsa, nuovoStato, r.getCapacita());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore aggiornamento stato risorsa", e);
        }
    }

    public List<Risorsa> getTutteLeRisorse() {
        try {
            return dao.selectAllRisorse();
        } catch (Exception e) {
            throw new RuntimeException("Errore caricamento risorse", e);
        }
    }

    public List<Risorsa> getRisorseLibereByServizio(int idServizio) {
        try {
            return dao.selectRisorseByServizio(idServizio);
        } catch (Exception e) {
            throw new RuntimeException("Errore risorse libere servizio " + idServizio, e);
        }
    }

    public Risorsa getRisorsaById(int idRisorsa) {
        try {
            return dao.selectRisorsa(idRisorsa);
        } catch (Exception e) {
            throw new RuntimeException("Errore dettaglio risorsa " + idRisorsa, e);
        }
    }

    public boolean isRisorsaDisponibile(int idRisorsa, Timestamp dataOra) {
        try {
            return dao.isDisponibile(idRisorsa, dataOra);
        } catch (Exception e) {
            throw new RuntimeException("Errore check disponibilità", e);
        }
    }

    public boolean risorsaAppartieneAlServizio(int idRisorsa, int idServizio) {
        try {
            Risorsa r = dao.selectRisorsa(idRisorsa);
            return r != null && r.getIDServizio() == idServizio;
        } catch (Exception e) {
            throw new RuntimeException("Errore verifica risorsa-servizio", e);
        }
    }

}
