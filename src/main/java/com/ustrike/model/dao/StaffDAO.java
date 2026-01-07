package com.ustrike.model.dao;

import com.ustrike.model.dto.Staff;
import com.ustrike.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class StaffDAO {

    // CREATE
    // Prima di chiamare questo, puoi usare emailExists(email) per gestire "email già usata"
    public boolean doSave(Staff staff) {
        String sql = "INSERT INTO Staff (NomeStaff, CognomeStaff, Email, Password, Ruolo) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashedPassword = hashPassword(staff.getPassword());

            stmt.setString(1, staff.getNomeStaff());
            stmt.setString(2, staff.getCognomeStaff());
            stmt.setString(3, staff.getEmail());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, staff.getRuolo()); // 'Bowling' o 'GoKart'

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // READ by PK
    public Staff doRetrieveByKey(int idStaff) {
        String sql = "SELECT * FROM Staff WHERE IDStaff = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idStaff);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Staff s = new Staff();
                s.setIDStaff(rs.getInt("IDStaff"));
                s.setNomeStaff(rs.getString("NomeStaff"));
                s.setCognomeStaff(rs.getString("CognomeStaff"));
                s.setEmail(rs.getString("Email"));
                s.setPassword(rs.getString("Password"));
                s.setRuolo(rs.getString("Ruolo"));
                return s;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ by email
    public Staff doRetrieveByEmail(String email) {
        if (email == null) return null;
        Staff s = null;
        String sql = "SELECT * FROM Staff WHERE Email = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                s = new Staff();
                s.setIDStaff(rs.getInt("IDStaff"));
                s.setNomeStaff(rs.getString("NomeStaff"));
                s.setCognomeStaff(rs.getString("CognomeStaff"));
                s.setEmail(rs.getString("Email"));
                s.setPassword(rs.getString("Password"));
                s.setRuolo(rs.getString("Ruolo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }

    // CHECK: esiste già una riga con questa email?
    public boolean emailExists(String email) {
        if (email == null) return false;
        String sql = "SELECT 1 FROM Staff WHERE Email = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true se c'è almeno una riga
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // LOGIN staff
    public Staff doRetrieveByCredentials(String email, String password) {
        String sql = "SELECT * FROM Staff WHERE Email = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, hashPassword(password));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Staff s = new Staff();
                s.setIDStaff(rs.getInt("IDStaff"));
                s.setNomeStaff(rs.getString("NomeStaff"));
                s.setCognomeStaff(rs.getString("CognomeStaff"));
                s.setEmail(rs.getString("Email"));
                s.setRuolo(rs.getString("Ruolo"));
                return s;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // LIST di tutto lo staff
    public List<Staff> doRetrieveAll() {
        List<Staff> lista = new ArrayList<>();
        String sql = "SELECT * FROM Staff";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Staff s = new Staff();
                s.setIDStaff(rs.getInt("IDStaff"));
                s.setNomeStaff(rs.getString("NomeStaff"));
                s.setCognomeStaff(rs.getString("CognomeStaff"));
                s.setEmail(rs.getString("Email"));
                s.setPassword(rs.getString("Password"));
                s.setRuolo(rs.getString("Ruolo"));
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // LIST filtrata per ruolo ('Bowling' / 'GoKart')
    public List<Staff> doRetrieveByRuolo(String ruolo) {
        List<Staff> lista = new ArrayList<>();
        String sql = "SELECT * FROM Staff WHERE Ruolo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ruolo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Staff s = new Staff();
                s.setIDStaff(rs.getInt("IDStaff"));
                s.setNomeStaff(rs.getString("NomeStaff"));
                s.setCognomeStaff(rs.getString("CognomeStaff"));
                s.setEmail(rs.getString("Email"));
                s.setPassword(rs.getString("Password"));
                s.setRuolo(rs.getString("Ruolo"));
                lista.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // UPDATE
    public boolean doUpdate(Staff staff) {
        String sql = "UPDATE Staff SET NomeStaff=?, CognomeStaff=?, Email=?, Password=?, Ruolo=? " +
                     "WHERE IDStaff=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getNomeStaff());
            stmt.setString(2, staff.getCognomeStaff());
            stmt.setString(3, staff.getEmail());
            stmt.setString(4, staff.getPassword()); // passa già hashata o gestisci hash qui
            stmt.setString(5, staff.getRuolo());
            stmt.setInt(6, staff.getIDStaff());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE by id
    public boolean doDelete(int idStaff) {
        String sql = "DELETE FROM Staff WHERE IDStaff = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idStaff);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE by DTO
    public boolean doDelete(Staff staff) {
        return doDelete(staff.getIDStaff());
    }

    // hashing password
    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }
}