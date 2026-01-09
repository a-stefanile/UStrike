package com.ustrike.model.dao;

import com.ustrike.model.dto.Staff;
import com.ustrike.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public boolean doSave(Staff staff) throws SQLException {
        String sql = "INSERT INTO Staff (NomeStaff, CognomeStaff, Email, PasswordHash, Ruolo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, staff.getNomeStaff());
            ps.setString(2, staff.getCognomeStaff());
            ps.setString(3, staff.getEmail());
            ps.setString(4, staff.getPasswordHash()); // già hashata (PBKDF2 salt:hash)
            ps.setString(5, staff.getRuolo());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) staff.setIDStaff(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public Staff doRetrieveByKey(int idStaff) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE IDStaff = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idStaff);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToStaff(rs) : null;
            }
        }
    }

    public Staff doRetrieveByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE Email = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToStaff(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE Email = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Staff> doRetrieveAll() throws SQLException {
        List<Staff> lista = new ArrayList<>();
        String sql = "SELECT * FROM Staff ORDER BY CognomeStaff, NomeStaff";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToStaff(rs));
            }
        }
        return lista;
    }

    public List<Staff> doRetrieveByRuolo(String ruolo) throws SQLException {
        List<Staff> lista = new ArrayList<>();
        String sql = "SELECT * FROM Staff WHERE Ruolo = ? ORDER BY CognomeStaff, NomeStaff";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ruolo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToStaff(rs));
                }
            }
        }
        return lista;
    }

    public boolean doUpdate(Staff staff) throws SQLException {
        String sql = "UPDATE Staff SET NomeStaff=?, CognomeStaff=?, Email=?, PasswordHash=?, Ruolo=? WHERE IDStaff=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staff.getNomeStaff());
            ps.setString(2, staff.getCognomeStaff());
            ps.setString(3, staff.getEmail());
            ps.setString(4, staff.getPasswordHash()); // già hashata
            ps.setString(5, staff.getRuolo());
            ps.setInt(6, staff.getIDStaff());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int idStaff, String newPasswordHash) throws SQLException {
        String sql = "UPDATE Staff SET PasswordHash = ? WHERE IDStaff = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, idStaff);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean doDelete(int idStaff) throws SQLException {
        String sql = "DELETE FROM Staff WHERE IDStaff = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idStaff);
            return ps.executeUpdate() > 0;
        }
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setIDStaff(rs.getInt("IDStaff"));
        s.setNomeStaff(rs.getString("NomeStaff"));
        s.setCognomeStaff(rs.getString("CognomeStaff"));
        s.setEmail(rs.getString("Email"));
        s.setPasswordHash(rs.getString("PasswordHash"));
        s.setRuolo(rs.getString("Ruolo"));
        return s;
    }
}
