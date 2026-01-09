package com.ustrike.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class Prenotazione implements Serializable {
    private static final long serialVersionUID = 1L;

    private int IDPrenotazione;
    private Timestamp data;
    private Timestamp orario;
    private String statoPrenotazione;
    private String partecipanti;
    private int IDServizio;
    private int IDRisorsa;
    private int IDCliente;
    private Integer IDStaff;

    public Prenotazione() {}

    public Prenotazione(int IDPrenotazione, Timestamp data, Timestamp orario, String statoPrenotazione,
                        String partecipanti, int IDServizio, int IDRisorsa, int IDCliente, Integer IDStaff) {
        this.IDPrenotazione = IDPrenotazione;
        this.data = data;
        this.orario = orario;
        this.statoPrenotazione = statoPrenotazione;
        this.partecipanti = partecipanti;
        this.IDServizio = IDServizio;
        this.IDRisorsa = IDRisorsa;
        this.IDCliente = IDCliente;
        this.IDStaff = IDStaff;
    }

    public int getIDPrenotazione() { return IDPrenotazione; }
    public void setIDPrenotazione(int iDPrenotazione) { IDPrenotazione = iDPrenotazione; }

    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }

    public Timestamp getOrario() { return orario; }
    public void setOrario(Timestamp orario) { this.orario = orario; }

    public String getStatoPrenotazione() { return statoPrenotazione; }
    public void setStatoPrenotazione(String statoPrenotazione) { this.statoPrenotazione = statoPrenotazione; }

    public String getPartecipanti() { return partecipanti; }
    public void setPartecipanti(String partecipanti) { this.partecipanti = partecipanti; }

    public int getIDServizio() { return IDServizio; }
    public void setIDServizio(int iDServizio) { IDServizio = iDServizio; }

    public int getIDRisorsa() { return IDRisorsa; }
    public void setIDRisorsa(int iDRisorsa) { IDRisorsa = iDRisorsa; }

    public int getIDCliente() { return IDCliente; }
    public void setIDCliente(int iDCliente) { IDCliente = iDCliente; }

    public Integer getIDStaff() { return IDStaff; }
    public void setIDStaff(Integer iDStaff) { IDStaff = iDStaff; }
}
