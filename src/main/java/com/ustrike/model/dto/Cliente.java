package com.ustrike.model.dto;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idCliente;
    private String nomeCliente;
    private String cognomeCliente;
    private String email;
    private String passwordHash;  // ← Hash!
    private int puntiTicket;

    public Cliente() {}
    
    public Cliente(int idCliente, String nomeCliente, String cognomeCliente, 
                   String email, String passwordHash, int puntiTicket) {
        this.idCliente = idCliente;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.email = email;
        this.passwordHash = passwordHash;
        this.puntiTicket = puntiTicket;
    }

    // Getters/Setters
    public int getIDCliente() { return idCliente; }
    public void setIDCliente(int idCliente) { this.idCliente = idCliente; }
    
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    
    public String getCognomeCliente() { return cognomeCliente; }
    public void setCognomeCliente(String cognomeCliente) { this.cognomeCliente = cognomeCliente; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public int getPuntiTicket() { return puntiTicket; }
    public void setPuntiTicket(int puntiTicket) { this.puntiTicket = puntiTicket; }
    
    public String getFullName() { return nomeCliente + " " + cognomeCliente; }
}
