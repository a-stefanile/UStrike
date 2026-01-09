package com.ustrike.model.dao;

import com.ustrike.model.dto.Risorsa;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RisorsaDAO {

    public int insertRisorsa(int stato, int capacita, int idServizio) throws SQLException {
        String SQL = "INSERT INTO Risorsa (Stato, Capacita, IDServizio) VALUES (?, ?, ?);";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, stato);
            ps.setInt(2, capacita);
            ps.setInt(3, idServizio);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);  
                    }
                }
            }
            return -1;
        }
    }

    public Risorsa selectRisorsa(int idRisorsa) throws SQLException {
        String SQL = "SELECT * FROM Risorsa WHERE IDRisorsa = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToRisorsa(rs);
            }
        }
        return null;
    }

    public List<Risorsa> selectAllRisorse() throws SQLException {
        List<Risorsa> risorse = new ArrayList<>();
        String SQL = "SELECT * FROM Risorsa ORDER BY IDServizio, Capacita;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                risorse.add(mapResultSetToRisorsa(rs));
            }
        }
        return risorse;
    }

    public List<Risorsa> selectRisorseByServizio(int idServizio) throws SQLException {
        List<Risorsa> risorse = new ArrayList<>();
        String SQL = "SELECT * FROM Risorsa WHERE IDServizio = ? AND Stato = 1 ORDER BY Capacita ASC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, idServizio);
            while (rs.next()) {
                risorse.add(mapResultSetToRisorsa(rs));  
            }
        }
        return risorse;
    }

    public boolean updateRisorsa(int idRisorsa, int stato, int capacita) throws SQLException {
        String SQL = "UPDATE Risorsa SET Stato = ?, Capacita = ? WHERE IDRisorsa = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, stato);
            ps.setInt(2, capacita);
            ps.setInt(3, idRisorsa);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRisorsa(int idRisorsa) throws SQLException {
        String SQL = "DELETE FROM Risorsa WHERE IDRisorsa = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idRisorsa);
            return ps.executeUpdate() > 0;
        }
    }

    
    public boolean isDisponibile(int idRisorsa, Timestamp dataOra) throws SQLException {
        String SQL = """
            SELECT COUNT(*) FROM Prenotazione p 
            WHERE p.IDRisorsa = ? AND p.StatoPrenotazione != 'Rifiutata' 
            AND (p.Data = ? OR p.Orario = ?);
            """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idRisorsa);
            ps.setTimestamp(2, dataOra);
            ps.setTimestamp(3, dataOra);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;  // No overlap
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
