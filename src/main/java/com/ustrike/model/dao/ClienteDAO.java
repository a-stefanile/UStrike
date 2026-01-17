package com.ustrike.model.dao;

import com.ustrike.model.dto.Cliente;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente selectClienteById(int idCliente) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE IDCliente = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToCliente(rs) : null;
            }
        }
    }

    public Cliente selectClienteByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE Email = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToCliente(rs) : null;
            }
        }
    }

    public List<Cliente> selectAllClienti() throws SQLException {
        List<Cliente> clienti = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY CognomeCliente, NomeCliente";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clienti.add(mapResultSetToCliente(rs));
            }
        }
        return clienti;
    }

    public boolean insertCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Cliente (NomeCliente, CognomeCliente, Email, PasswordHash, PuntiTicket) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNomeCliente());
            ps.setString(2, cliente.getCognomeCliente());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getPasswordHash());
            ps.setInt(5, cliente.getPuntiTicket());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) cliente.setIDCliente(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE Cliente SET NomeCliente=?, CognomeCliente=?, Email=?, PuntiTicket=? WHERE IDCliente=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cliente.getNomeCliente());
            ps.setString(2, cliente.getCognomeCliente());
            ps.setString(3, cliente.getEmail());
            ps.setInt(4, cliente.getPuntiTicket());
            ps.setInt(5, cliente.getIDCliente());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int idCliente, String newPasswordHash) throws SQLException {
        String sql = "UPDATE Cliente SET PasswordHash = ? WHERE IDCliente = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM Cliente WHERE IDCliente = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
                rs.getString("PasswordHash"),
                rs.getInt("PuntiTicket")
        );
    }
}
