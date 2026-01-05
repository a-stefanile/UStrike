package com.ustrike.model.dto;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idCliente;
    private String nomeCliente;
    private String cognomeCliente;
    private String email;
    private String password;
    private int puntiTicket;

    public Cliente() {}
    
    public Cliente(int idCliente, String nomeCliente, String cognomeCliente, 
                   String email, String password, int puntiTicket) {
        this.idCliente = idCliente;
        this.nomeCliente = nomeCliente;
        this.cognomeCliente = cognomeCliente;
        this.email = email;
        this.password = password;
        this.puntiTicket = puntiTicket;
    }

    // Getter e Setter
    public int getIDCliente() { return idCliente; }
    public void setIDCliente(int idCliente) { this.idCliente = idCliente; }
    
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    
    public String getCognomeCliente() { return cognomeCliente; }
    public void setCognomeCliente(String cognomeCliente) { this.cognomeCliente = cognomeCliente; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getPuntiTicket() { return puntiTicket; }
    public void setPuntiTicket(int puntiTicket) { this.puntiTicket = puntiTicket; }
}
