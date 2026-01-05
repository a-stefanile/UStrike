package com.ustrike.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class Turno implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idTurno;
    private Timestamp data;
    private String fasciaOraria;  
    private int idStaff;
    private int idManager;

    public Turno() {}
    
    public Turno(int idTurno, Timestamp data, String fasciaOraria, 
                 int idStaff, int idManager) {
        this.idTurno = idTurno;
        this.data = data;
        this.fasciaOraria = fasciaOraria;
        this.idStaff = idStaff;
        this.idManager = idManager;
    }

    // Getter/Setter
    public int getIDTurno() { return idTurno; }
    public void setIDTurno(int idTurno) { this.idTurno = idTurno; }
    
    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }
    
    public String getFasciaOraria() { return fasciaOraria; }
    public void setFasciaOraria(String fasciaOraria) { this.fasciaOraria = fasciaOraria; }
    
    public int getIDStaff() { return idStaff; }
    public void setIDStaff(int idStaff) { this.idStaff = idStaff; }
    
    public int getIDManager() { return idManager; }
    public void setIDManager(int idManager) { this.idManager = idManager; }
}
