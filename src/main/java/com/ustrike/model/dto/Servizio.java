package com.ustrike.model.dto;

import java.io.Serializable;

public class Servizio implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idServizio;
    private String nomeServizio;
    private boolean statoServizio;  

    public Servizio() {}
    
    public Servizio(int idServizio, String nomeServizio, boolean statoServizio) {
        this.idServizio = idServizio;
        this.nomeServizio = nomeServizio;
        this.statoServizio = statoServizio;
    }

    // Getter e Setter
    public int getIDServizio() { return idServizio; }
    public void setIDServizio(int idServizio) { this.idServizio = idServizio; }
    
    public String getNomeServizio() { return nomeServizio; }
    public void setNomeServizio(String nomeServizio) { this.nomeServizio = nomeServizio; }
    
    public boolean getStatoServizio() { return statoServizio; }
    public void setStatoServizio(boolean statoServizio) { this.statoServizio = statoServizio; }
}
