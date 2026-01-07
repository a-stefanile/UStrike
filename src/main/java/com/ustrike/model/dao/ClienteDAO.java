package com.ustrike.model.dao;

import com.ustrike.model.dto.Cliente;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente selectClienteById(int idCliente) throws SQLException {
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
        String SQL = "SELECT * FROM Cliente ORDER BY Cognome, Nome;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clienti.add(mapResultSetToCliente(rs));
            }
        }
        return clienti;
    }

    public boolean insertCliente(Cliente cliente) throws SQLException {
        String SQL = "INSERT INTO Cliente (Nome, Cognome, Email, PasswordHash, PuntiTicket) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNomeCliente());
            ps.setString(2, cliente.getCognomeCliente());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getPasswordHash());
            ps.setInt(5, cliente.getPuntiTicket());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIDCliente(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateCliente(Cliente cliente) throws SQLException {
        String SQL = "UPDATE Cliente SET Nome = ?, Cognome = ?, Email = ?, PuntiTicket = ? WHERE IDCliente = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, cliente.getNomeCliente());
            ps.setString(2, cliente.getCognomeCliente());
            ps.setString(3, cliente.getEmail());
            ps.setInt(4, cliente.getPuntiTicket());
            ps.setInt(5, cliente.getIDCliente());
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int idCliente, String newPasswordHash) throws SQLException {
        String SQL = "UPDATE Cliente SET PasswordHash = ? WHERE IDCliente = ?;";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL)) {
            ps.setString(1, newPasswordHash);
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
            rs.getString("Nome"),
            rs.getString("Cognome"),
            rs.getString("Email"),
            rs.getString("PasswordHash"),
            rs.getInt("PuntiTicket")
        );
    }
}
