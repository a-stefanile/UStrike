package com.ustrike.model.dto;

import java.io.Serializable;

public class Manager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idManager;
    private String nomeManager;
    private String cognomeManager;
    private String email;
    private String password;

    public Manager() {}
    
    public Manager(int idManager, String nomeManager, String cognomeManager, 
                   String email, String password) {
        this.idManager = idManager;
        this.nomeManager = nomeManager;
        this.cognomeManager = cognomeManager;
        this.email = email;
        this.password = password;
    }

    // Getter e Setter
    public int getIDManager() { return idManager; }
    public void setIDManager(int idManager) { this.idManager = idManager; }
    
    public String getNomeManager() { return nomeManager; }
    public void setNomeManager(String nomeManager) { this.nomeManager = nomeManager; }
    
    public String getCognomeManager() { return cognomeManager; }
    public void setCognomeManager(String cognomeManager) { this.cognomeManager = cognomeManager; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
