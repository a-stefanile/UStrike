package com.ustrike.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class Prenotazione implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idPrenotazione;
    private Timestamp data;              
    private Timestamp orario;            
    private String statoPrenotazione;    
    private String partecipanti;         
    private int idServizio;              
    private int idRisorsa;               
    private int idCliente;
    private Integer idStaff;             

    
    public Prenotazione() {}

    
    public Prenotazione(int idPrenotazione, Timestamp data, Timestamp orario,
                       String statoPrenotazione, String partecipanti,
                       int idServizio, int idRisorsa, int idCliente, Integer idStaff) {
        this.idPrenotazione = idPrenotazione;
        this.data = data;
        this.orario = orario;
        this.statoPrenotazione = statoPrenotazione;
        this.partecipanti = partecipanti;
        this.idServizio = idServizio;
        this.idRisorsa = idRisorsa;
        this.idCliente = idCliente;
        this.idStaff = idStaff;
    }

    // Getter e Setter
    public int getIDPrenotazione() { return idPrenotazione; }
    public void setIDPrenotazione(int idPrenotazione) { this.idPrenotazione = idPrenotazione; }

    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }

    public Timestamp getOrario() { return orario; }
    public void setOrario(Timestamp orario) { this.orario = orario; }

    public String getStatoPrenotazione() { return statoPrenotazione; }
    public void setStatoPrenotazione(String statoPrenotazione) { this.statoPrenotazione = statoPrenotazione; }

    public String getPartecipanti() { return partecipanti; }
    public void setPartecipanti(String partecipanti) { this.partecipanti = partecipanti; }

    public int getIDServizio() { return idServizio; }
    public void setIDServizio(int idServizio) { this.idServizio = idServizio; }

    public int getIDRisorsa() { return idRisorsa; }
    public void setIDRisorsa(int idRisorsa) { this.idRisorsa = idRisorsa; }

    public int getIDCliente() { return idCliente; }
    public void setIDCliente(int idCliente) { this.idCliente = idCliente; }

    public Integer getIDStaff() { return idStaff; }
    public void setIDStaff(Integer idStaff) { this.idStaff = idStaff; }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "IDPrenotazione=" + idPrenotazione +
                ", Data=" + data +
                ", Orario=" + orario +
                ", Stato='" + statoPrenotazione + '\'' +
                ", Partecipanti='" + partecipanti + '\'' +
                ", IDServizio=" + idServizio +
                ", IDRisorsa=" + idRisorsa +
                ", IDCliente=" + idCliente +
                ", IDStaff=" + idStaff +
                '}';
    }
}
