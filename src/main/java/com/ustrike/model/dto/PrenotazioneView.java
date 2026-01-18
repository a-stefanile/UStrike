package com.ustrike.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class PrenotazioneView implements Serializable {
    private static final long serialVersionUID = 1L;

    private int IDPrenotazione;
    private Timestamp data;
    private Timestamp orario;
    private String statoPrenotazione;
    private String partecipanti;

    private int IDServizio;
    private String nomeServizio;

    private int IDRisorsa;
    private int capacitaRisorsa;

    private Integer IDStaff;   // nullable
    private String noteStaff;  // nullable (motivo rifiuto / nota staff)

    public PrenotazioneView() {
    }

    public int getIDPrenotazione() { return IDPrenotazione; }
    public void setIDPrenotazione(int IDPrenotazione) { this.IDPrenotazione = IDPrenotazione; }

    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }

    public Timestamp getOrario() { return orario; }
    public void setOrario(Timestamp orario) { this.orario = orario; }

    public String getStatoPrenotazione() { return statoPrenotazione; }
    public void setStatoPrenotazione(String statoPrenotazione) { this.statoPrenotazione = statoPrenotazione; }

    public String getPartecipanti() { return partecipanti; }
    public void setPartecipanti(String partecipanti) { this.partecipanti = partecipanti; }

    public int getIDServizio() { return IDServizio; }
    public void setIDServizio(int IDServizio) { this.IDServizio = IDServizio; }

    public String getNomeServizio() { return nomeServizio; }
    public void setNomeServizio(String nomeServizio) { this.nomeServizio = nomeServizio; }

    public int getIDRisorsa() { return IDRisorsa; }
    public void setIDRisorsa(int IDRisorsa) { this.IDRisorsa = IDRisorsa; }

    public int getCapacitaRisorsa() { return capacitaRisorsa; }
    public void setCapacitaRisorsa(int capacitaRisorsa) { this.capacitaRisorsa = capacitaRisorsa; }

    public Integer getIDStaff() { return IDStaff; }
    public void setIDStaff(Integer IDStaff) { this.IDStaff = IDStaff; }

    public String getNoteStaff() { return noteStaff; }
    public void setNoteStaff(String noteStaff) { this.noteStaff = noteStaff; }
}
