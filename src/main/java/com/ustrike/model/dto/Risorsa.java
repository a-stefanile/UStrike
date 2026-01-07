package com.ustrike.model.dto;

import java.io.Serializable;

public class Risorsa implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idRisorsa;
    private int stato;       
    private int capacita;    
    private int idServizio;  

    
    public Risorsa() {}

    
    public Risorsa(int idRisorsa, int stato, int capacita, int idServizio) {
        this.idRisorsa = idRisorsa;
        this.stato = stato;
        this.capacita = capacita;
        this.idServizio = idServizio;
    }

    
    public int getIDRisorsa() { return idRisorsa; }
    public void setIDRisorsa(int idRisorsa) { this.idRisorsa = idRisorsa; }

    public int getStato() { return stato; }
    public void setStato(int stato) { this.stato = stato; }

    public int getCapacita() { return capacita; }
    public void setCapacita(int capacita) { this.capacita = capacita; }

    public int getIDServizio() { return idServizio; }
    public void setIDServizio(int idServizio) { this.idServizio = idServizio; }

    @Override
    public String toString() {
        return "Risorsa{" +
                "IDRisorsa=" + idRisorsa +
                ", Stato=" + stato +
                ", Capacita=" + capacita +
                ", IDServizio=" + idServizio +
                '}';
    }
}
