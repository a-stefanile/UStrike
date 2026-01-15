package com.ustrike.model.dao;

import com.ustrike.model.dto.Risorsa;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RisorsaDAO {

    public int insertRisorsa(int stato, int capacita, int idServizio) throws SQLException {
        String sql = "INSERT INTO Risorsa (Stato, Capacita, IDServizio) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, stato);
            ps.setInt(2, capacita);
            ps.setInt(3, idServizio);

            int rows = ps.executeUpdate();
            if (rows <= 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Risorsa selectRisorsa(int idRisorsa) throws SQLException {
        String sql = "SELECT * FROM Risorsa WHERE IDRisorsa = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idRisorsa);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToRisorsa(rs) : null;
            }
        }
    }

    public List<Risorsa> selectAllRisorse() throws SQLException {
        List<Risorsa> risorse = new ArrayList<>();
        String sql = "SELECT * FROM Risorsa ORDER BY IDServizio, Capacita";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                risorse.add(mapResultSetToRisorsa(rs));
            }
        }
        return risorse;
    }

    public List<Risorsa> selectRisorseByServizio(int idServizio) throws SQLException {
        List<Risorsa> risorse = new ArrayList<>();
        String sql = "SELECT * FROM Risorsa WHERE IDServizio = ? AND Stato = 1 ORDER BY Capacita ASC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idServizio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risorse.add(mapResultSetToRisorsa(rs));
                }
            }
        }
        return risorse;
    }

    public boolean updateRisorsa(int idRisorsa, int stato, int capacita) throws SQLException {
        String sql = "UPDATE Risorsa SET Stato = ?, Capacita = ? WHERE IDRisorsa = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, stato);
            ps.setInt(2, capacita);
            ps.setInt(3, idRisorsa);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRisorsa(int idRisorsa) throws SQLException {
        String sql = "DELETE FROM Risorsa WHERE IDRisorsa = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idRisorsa);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Disponibilità su uno specifico orario:
     * la risorsa NON è disponibile se esiste una prenotazione con lo stesso Orario
     * e stato diverso da Rifiutata/Annullata.
     */
    public boolean isDisponibile(int idRisorsa, Timestamp orario) throws SQLException {
        // Confronto solo data + ora (HH), ignorando minuti e secondi
        String sql = """
            SELECT COUNT(*)
            FROM Prenotazione p
            WHERE p.IDRisorsa = ?
              AND DATE(p.Orario) = ?
              AND HOUR(p.Orario) = ?
              AND p.StatoPrenotazione NOT IN ('Rifiutata', 'Annullata')
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idRisorsa);
            ps.setDate(2, new java.sql.Date(orario.getTime()));  // solo giorno/mese/anno
            ps.setInt(3, orario.toLocalDateTime().getHour());   // solo ora

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }
    }


    private Risorsa mapResultSetToRisorsa(ResultSet rs) throws SQLException {
        return new Risorsa(
                rs.getInt("IDRisorsa"),
                rs.getInt("Stato"),
                rs.getInt("Capacita"),
                rs.getInt("IDServizio")
        );
    }
}
