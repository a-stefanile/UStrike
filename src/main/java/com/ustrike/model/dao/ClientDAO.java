package com.ustrike.model.dao;

import com.ustrike.model.dto.Cliente;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public void insertCliente(String nome, String cognome, String email, String password) throws SQLException {
        String SQL = "INSERT INTO Cliente (NomeCliente, CognomeCliente, Email, Password, PuntiTicket) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setInt(5, 0); 
            ps.executeUpdate();
        }
    }

    public Cliente selectCliente(int idCliente) throws SQLException {
        String SQL = "SELECT * FROM Cliente WHERE IDCliente = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
        }
        return null;
    }

    public Cliente selectClienteByEmail(String email) throws SQLException {
        String SQL = "SELECT * FROM Cliente WHERE Email = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
        }
        return null;
    }

    public List<Cliente> selectAllClienti() throws SQLException {
        List<Cliente> clienti = new ArrayList<>();
        String SQL = "SELECT * FROM Cliente ORDER BY CognomeCliente ASC;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clienti.add(mapResultSetToCliente(rs));
            }
        }
        return clienti;
    }

    public boolean updateProfiloCliente(int idCliente, String nome, String cognome, String email) throws SQLException {
        String SQL = "UPDATE Cliente SET NomeCliente = ?, CognomeCliente = ?, Email = ? WHERE IDCliente = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, nome);
            ps.setString(2, cognome);
            ps.setString(3, email);
            ps.setInt(4, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePuntiTicket(int idCliente, int nuoviPunti) throws SQLException {
        String SQL = "UPDATE Cliente SET PuntiTicket = ? WHERE IDCliente = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, nuoviPunti);
            ps.setInt(2, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCliente(int idCliente) throws SQLException {
        String SQL = "DELETE FROM Cliente WHERE IDCliente = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("IDCliente"),
            rs.getString("NomeCliente"),
            rs.getString("CognomeCliente"),
            rs.getString("Email"),
            rs.getString("Password"),
            rs.getInt("PuntiTicket")
        );
    }
}