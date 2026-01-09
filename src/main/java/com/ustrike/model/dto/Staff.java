package com.ustrike.model.dto;

import java.io.Serializable;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idStaff;
    private String nomeStaff;
    private String cognomeStaff;
    private String email;
    private String passwordHash;  
    private String ruolo;         

    public Staff() {}
    
    public Staff(int idStaff, String nomeStaff, String cognomeStaff, 
                 String email, String passwordHash, String ruolo) {
        this.idStaff = idStaff;
        this.nomeStaff = nomeStaff;
        this.cognomeStaff = cognomeStaff;
        this.email = email;
        this.passwordHash = passwordHash;
        this.ruolo = ruolo;
    }

    // Getters/Setters
    public int getIDStaff() { return idStaff; }
    public void setIDStaff(int idStaff) { this.idStaff = idStaff; }
    
    public String getNomeStaff() { return nomeStaff; }
    public void setNomeStaff(String nomeStaff) { this.nomeStaff = nomeStaff; }
    
    public String getCognomeStaff() { return cognomeStaff; }
    public void setCognomeStaff(String cognomeStaff) { this.cognomeStaff = cognomeStaff; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
    
    
    public String getFullName() { 
        return nomeStaff + " " + cognomeStaff; 
    }
    
    
  }
