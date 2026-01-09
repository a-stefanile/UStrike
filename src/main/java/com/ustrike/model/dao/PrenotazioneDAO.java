package com.ustrike.model.dao;

import com.ustrike.model.dto.Prenotazione;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {

    public int insertPrenotazione(Timestamp data, Timestamp orario, String stato,
                                  String partecipanti, int idServizio, int idRisorsa,
                                  int idCliente, Integer idStaff) throws SQLException {

        String sql = "INSERT INTO Prenotazione " +
                "(Data, Orario, StatoPrenotazione, Partecipanti, IDServizio, IDRisorsa, IDCliente, IDStaff) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, data);
            ps.setTimestamp(2, orario);
            ps.setString(3, stato);
            ps.setString(4, partecipanti);
            ps.setInt(5, idServizio);
            ps.setInt(6, idRisorsa);
            ps.setInt(7, idCliente);
            if (idStaff != null) ps.setInt(8, idStaff);
            else ps.setNull(8, Types.INTEGER);

            int rows = ps.executeUpdate();
            if (rows <= 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Prenotazione selectPrenotazione(int idPrenotazione) throws SQLException {
        String sql = "SELECT * FROM Prenotazione WHERE IDPrenotazione = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPrenotazione);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToPrenotazione(rs) : null;
            }
        }
    }

    public List<Prenotazione> selectPrenotazioniByCliente(int idCliente) throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione WHERE IDCliente = ? ORDER BY Data DESC, Orario DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCliente); // ✅ mancava nel tuo file

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectPrenotazioniInAttesa() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione WHERE StatoPrenotazione = 'In attesa' ORDER BY Data ASC, Orario ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) prenotazioni.add(mapResultSetToPrenotazione(rs));
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectPrenotazioniCompletate() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione " +
                     "WHERE StatoPrenotazione IN ('Confermata', 'Rifiutata') " +
                     "ORDER BY Data DESC, Orario DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) prenotazioni.add(mapResultSetToPrenotazione(rs));
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectAllPrenotazioni() throws SQLException {
        List<Prenotazione> tutte = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione ORDER BY Data DESC, Orario ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) tutte.add(mapResultSetToPrenotazione(rs));
        }
        return tutte;
    }

    public boolean updateStatoPrenotazione(int idPrenotazione, String nuovoStato, Integer idStaff) throws SQLException {
        // ✅ Protezione: si può gestire solo se era ancora "In attesa"
        String sql = "UPDATE Prenotazione SET StatoPrenotazione = ?, IDStaff = ? " +
                     "WHERE IDPrenotazione = ? AND StatoPrenotazione = 'In attesa'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);
            if (idStaff != null) ps.setInt(2, idStaff);
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, idPrenotazione);

            return ps.executeUpdate() > 0;
        }
    }

    private Prenotazione mapResultSetToPrenotazione(ResultSet rs) throws SQLException {
        Integer idStaff = (rs.getObject("IDStaff") != null) ? rs.getInt("IDStaff") : null;

        return new Prenotazione(
                rs.getInt("IDPrenotazione"),
                rs.getTimestamp("Data"),
                rs.getTimestamp("Orario"),
                rs.getString("StatoPrenotazione"),
                rs.getString("Partecipanti"),
                rs.getInt("IDServizio"),
                rs.getInt("IDRisorsa"),
                rs.getInt("IDCliente"),
                idStaff
        );
    }
}
