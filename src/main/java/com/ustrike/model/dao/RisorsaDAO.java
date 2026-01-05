package com.ustrike.dao;

import com.ustrike.model.dto.Risorsa;
import com.ustrike.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RisorsaDAO {

    public void insertRisorsa(int stato, int capacita, int idServizio) throws SQLException {
        String SQL = "INSERT INTO Risorsa (Stato, Capacita, IDServizio) VALUES (?, ?, ?);";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, stato);
            ps.setInt(2, capacita);
            ps.setInt(3, idServizio);
            ps.executeUpdate();
        }
    }

    public Risorsa selectRisorsa(int idRisorsa) throws SQLException {
        String SQL = "SELECT * FROM Risorsa WHERE IDRisorsa = ?;";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idRisorsa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Risorsa(
                    rs.getInt("IDRisorsa"),
                    rs.getInt("Stato"),
                    rs.getInt("Capacita"),
                    rs.getInt("IDServizio")
                );
            }
        }
        return null;
    }

    public List<Risorsa> selectAllRisorse() throws SQLException {
        List<Risorsa> risorse = new ArrayList<>();
        String SQL = "SELECT * FROM Risorsa;";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                risorse.add(new Risorsa(
                    rs.getInt("IDRisorsa"),
                    rs.getInt("Stato"),
                    rs.getInt("Capacita"),
                    rs.getInt("IDServizio")
                ));
            }
        }
        return risorse;
    }

    public List<Risorsa> selectRisorseByServizio(int idServizio) throws SQLException {
        List<Risorsa> risorse = new ArrayList<>();
        String SQL = "SELECT * FROM Risorsa WHERE IDServizio = ? AND Stato = 1 ORDER BY Capacita;";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idServizio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                risorse.add(new Risorsa(
                    rs.getInt("IDRisorsa"),
                    rs.getInt("Stato"),
                    rs.getInt("Capacita"),
                    idServizio
                ));
            }
        }
        return risorse;
    }

    public boolean updateRisorsa(int idRisorsa, int stato, int capacita) throws SQLException {
        String SQL = "UPDATE Risorsa SET Stato = ?, Capacita = ? WHERE IDRisorsa = ?;";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, stato);
            ps.setInt(2, capacita);
            ps.setInt(3, idRisorsa);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRisorsa(int idRisorsa) throws SQLException {
        String SQL = "DELETE FROM Risorsa WHERE IDRisorsa = ?;";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idRisorsa);
            return ps.executeUpdate() > 0;
        }
    }
}