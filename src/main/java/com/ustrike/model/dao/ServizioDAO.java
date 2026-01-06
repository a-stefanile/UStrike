package com.ustrike.model.dao;

import com.ustrike.model.dto.Servizio;
import com.ustrike.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServizioDAO {

    public boolean doSave(Servizio servizio) {
        String sql = "INSERT INTO Servizio (NomeServizio, StatoServizio) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servizio.getNomeServizio());
            stmt.setBoolean(2, servizio.getStatoServizio()); 

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Servizio doRetrieveByKey(int idServizio) {
        String sql = "SELECT * FROM Servizio WHERE IDServizio = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idServizio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Servizio s = new Servizio();
                s.setIDServizio(rs.getInt("IDServizio"));
                s.setNomeServizio(rs.getString("NomeServizio"));
                s.setStatoServizio(rs.getBoolean("StatoServizio"));
                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Servizio doRetrieveByNome(String nomeServizio) {
        String sql = "SELECT * FROM Servizio WHERE NomeServizio = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeServizio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Servizio s = new Servizio();
                s.setIDServizio(rs.getInt("IDServizio"));
                s.setNomeServizio(rs.getString("NomeServizio"));
                s.setStatoServizio(rs.getBoolean("StatoServizio"));
                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Servizio> doRetrieveAll() {
        List<Servizio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Servizio";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servizio s = new Servizio();
                s.setIDServizio(rs.getInt("IDServizio"));
                s.setNomeServizio(rs.getString("NomeServizio"));
                s.setStatoServizio(rs.getBoolean("StatoServizio"));
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Servizio> doRetrieveEnabled() {
        List<Servizio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Servizio WHERE StatoServizio = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servizio s = new Servizio();
                s.setIDServizio(rs.getInt("IDServizio"));
                s.setNomeServizio(rs.getString("NomeServizio"));
                s.setStatoServizio(rs.getBoolean("StatoServizio"));
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean doUpdate(Servizio servizio) {
        String sql = "UPDATE Servizio SET NomeServizio = ?, StatoServizio = ? " +
                     "WHERE IDServizio = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servizio.getNomeServizio());
            stmt.setBoolean(2, servizio.getStatoServizio());
            stmt.setInt(3, servizio.getIDServizio());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean abilitaServizio(int idServizio) {
        String sql = "UPDATE Servizio SET StatoServizio = 1 WHERE IDServizio = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idServizio);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disabilitaServizio(int idServizio) {
        String sql = "UPDATE Servizio SET StatoServizio = 0 WHERE IDServizio = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idServizio);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean doDelete(int idServizio) {
        String sql = "DELETE FROM Servizio WHERE IDServizio = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idServizio);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
