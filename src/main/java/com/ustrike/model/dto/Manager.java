package com.ustrike.model.dto;

import java.io.Serializable;

public class Manager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idManager;
    private String nomeManager;
    private String cognomeManager;
    private String email;
    private String passwordHash;  
    private String ruoloManager;  

    public Manager() {}
    
    public Manager(int idManager, String nomeManager, String cognomeManager, 
                   String email, String passwordHash, String ruoloManager) {
        this.idManager = idManager;
        this.nomeManager = nomeManager;
        this.cognomeManager = cognomeManager;
        this.email = email;
        this.passwordHash = passwordHash;
        this.ruoloManager = ruoloManager;
    }

    // Getters/Setters
    public int getIDManager() { return idManager; }
    public void setIDManager(int idManager) { this.idManager = idManager; }
    
    public String getNomeManager() { return nomeManager; }
    public void setNomeManager(String nomeManager) { this.nomeManager = nomeManager; }
    
    public String getCognomeManager() { return cognomeManager; }
    public void setCognomeManager(String cognomeManager) { this.cognomeManager = cognomeManager; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getRuoloManager() { return ruoloManager; }
    public void setRuoloManager(String ruoloManager) { this.ruoloManager = ruoloManager; }
    
    
    public String getFullName() { 
        return nomeManager + " " + cognomeManager; 
    }
}
