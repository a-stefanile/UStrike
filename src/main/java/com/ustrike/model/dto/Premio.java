package com.ustrike.model.dto;

import java.io.Serializable;

public class Premio implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idPremio;
    private String nomePremio;
    private String descrizionePremio;
    private int valoreTicket;
    private boolean stato;  

    public Premio() {}
    
    public Premio(int idPremio, String nomePremio, String descrizionePremio, 
                  int valoreTicket, boolean stato) {
        this.idPremio = idPremio;
        this.nomePremio = nomePremio;
        this.descrizionePremio = descrizionePremio;
        this.valoreTicket = valoreTicket;
        this.stato = stato;
    }

    
    public int getIDPremio() { return idPremio; }
    public void setIDPremio(int idPremio) { this.idPremio = idPremio; }
    
    public String getNomePremio() { return nomePremio; }
    public void setNomePremio(String nomePremio) { this.nomePremio = nomePremio; }
    
    public String getDescrizionePremio() { return descrizionePremio; }
    public void setDescrizionePremio(String descrizionePremio) { this.descrizionePremio = descrizionePremio; }
    
    public int getValoreTicket() { return valoreTicket; }
    public void setValoreTicket(int valoreTicket) { this.valoreTicket = valoreTicket; }
    
    public boolean getStato() { return stato; }
    public void setStato(boolean stato) { this.stato = stato; }
}
