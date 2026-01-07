package com.ustrike.model.dao;

import com.ustrike.model.dto.Prenotazione;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {

    public void insertPrenotazione(Timestamp data, Timestamp orario, String stato, 
                                   String partecipanti, int idServizio, int idRisorsa, 
                                   int idCliente, Integer idStaff) throws SQLException {
        String SQL = "INSERT INTO Prenotazione (Data, Orario, StatoPrenotazione, Partecipanti, IDServizio, IDRisorsa, IDCliente, IDStaff) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setTimestamp(1, data);
            ps.setTimestamp(2, orario);
            ps.setString(3, stato);
            ps.setString(4, partecipanti);
            ps.setInt(5, idServizio);
            ps.setInt(6, idRisorsa);
            ps.setInt(7, idCliente);
            if (idStaff != null) {
                ps.setInt(8, idStaff);
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    public Prenotazione selectPrenotazione(int idPrenotazione) throws SQLException {
        String SQL = "SELECT * FROM Prenotazione WHERE IDPrenotazione = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idPrenotazione);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPrenotazione(rs);
            }
        }
        return null;
    }

    public List<Prenotazione> selectPrenotazioniByCliente(int idCliente) throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String SQL = "SELECT * FROM Prenotazione WHERE IDCliente = ? ORDER BY Orario DESC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectPrenotazioniInAttesa() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String SQL = "SELECT * FROM Prenotazione WHERE StatoPrenotazione = 'In attesa' ORDER BY Orario ASC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectPrenotazioniByServizio(int idServizio) throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String SQL = "SELECT * FROM Prenotazione WHERE IDServizio = ? AND StatoPrenotazione = 'Confermata' ORDER BY Orario ASC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idServizio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        }
        return prenotazioni;
    }

    public boolean updateStatoPrenotazione(int idPrenotazione, String nuovoStato, Integer idStaff) throws SQLException {
        String SQL = "UPDATE Prenotazione SET StatoPrenotazione = ?, IDStaff = ? WHERE IDPrenotazione = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, nuovoStato);
            if (idStaff != null) {
                ps.setInt(2, idStaff);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, idPrenotazione);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePrenotazione(int idPrenotazione) throws SQLException {
        String SQL = "DELETE FROM Prenotazione WHERE IDPrenotazione = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idPrenotazione);
            return ps.executeUpdate() > 0;
        }
    }

    private Prenotazione mapResultSetToPrenotazione(ResultSet rs) throws SQLException {
        return new Prenotazione(
            rs.getInt("IDPrenotazione"),
            rs.getTimestamp("Data"),
            rs.getTimestamp("Orario"),
            rs.getString("StatoPrenotazione"),
            rs.getString("Partecipanti"),
            rs.getInt("IDServizio"),
            rs.getInt("IDRisorsa"),
            rs.getInt("IDCliente"),
            rs.getObject("IDStaff") != null ? rs.getInt("IDStaff") : null
        );
    }
    
    public List<Prenotazione> selectPrenotazioniCompletate() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String SQL = "SELECT * FROM Prenotazione WHERE StatoPrenotazione IN ('Confermata', 'Rifiutata') ORDER BY Orario DESC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        }
        return prenotazioni;
    }
    
    public List<Prenotazione> selectAllPrenotazioni() throws SQLException {
        List<Prenotazione> tutte = new ArrayList<>();
        String SQL = "SELECT * FROM Prenotazione ORDER BY Data DESC, Orario ASC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                tutte.add(mapResultSetToPrenotazione(rs));
            }
        }
        return tutte;
    }
}
