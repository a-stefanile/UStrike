package com.ustrike.model;

import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoPrenotazioni {
    private static CatalogoPrenotazioni instance;
    private List<Prenotazione> tuttePrenotazioni = new ArrayList<>();
    private final PrenotazioneDAO dao = new PrenotazioneDAO();

    // Singleton privato
    private CatalogoPrenotazioni() {
        refresh();
    }

    // Bill Pugh Singleton (thread-safe) [web:77]
    private static class SingletonHelper {
        private static final CatalogoPrenotazioni INSTANCE = new CatalogoPrenotazioni();
    }

    public static CatalogoPrenotazioni getInstance() {
        return SingletonHelper.INSTANCE;
    }

    // Metodi SDD
    public List<Prenotazione> getTuttePrenotazioni() {
        return new ArrayList<>(tuttePrenotazioni);
    }

    public List<Prenotazione> getPrenotazioniInAttesa() {
        return tuttePrenotazioni.stream()
                .filter(p -> "In attesa".equals(p.getStatoPrenotazione()))
                .collect(Collectors.toList());
    }

    public List<Prenotazione> getPrenotazioniConfermate() {
        return tuttePrenotazioni.stream()
                .filter(p -> "Confermata".equals(p.getStatoPrenotazione()))
                .collect(Collectors.toList());
    }

    public List<Prenotazione> cercaPerCliente(int idCliente) {
        return tuttePrenotazioni.stream()
                .filter(p -> p.getIDCliente() == idCliente)
                .collect(Collectors.toList());
    }

    public void refresh() {
        try {
            tuttePrenotazioni = dao.selectAllPrenotazioni();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyChange() {
        refresh();  // Refresh dopo insert/update/delete
    }
}
