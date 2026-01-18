package com.ustrike.control;

import com.ustrike.util.DBConnection;
import com.ustrike.util.PasswordHasher;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebListener
public class DataSeederListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	    String enabled = System.getProperty("seed.enabled", "false"); // default OFF
	    if (!"true".equalsIgnoreCase(enabled)) return;

	    try {
	        seedCoreStaff();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing
    }

    public static void seedCoreStaff() throws SQLException {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            seedStaff(conn,
                    "Marco", "Pausa",
                    "m.pausa@ustrike.staff.it",
                    "Break45!",
                    "Bowling"
            );

            seedStaff(conn,
                    "Luigi", "Maffi",
                    "l.maffi@ustrike.staff.it",
                    "Maffi80!",
                    "GoKart"
            );

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBConnection.closeConnection(conn);
            }
        }
    }

    private static void seedStaff(Connection conn,
                                  String nomeStaff,
                                  String cognomeStaff,
                                  String email,
                                  String plainPassword,
                                  String ruolo) throws SQLException {

        if (existsByEmail(conn, email)) return;

        String passwordHash = PasswordHasher.hash(plainPassword); 

        String sql = "INSERT INTO Staff (NomeStaff, CognomeStaff, Email, PasswordHash, Ruolo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeStaff);
            ps.setString(2, cognomeStaff);
            ps.setString(3, email);
            ps.setString(4, passwordHash);
            ps.setString(5, ruolo); // enum('Bowling','GoKart')
            ps.executeUpdate();
        }
    }

    private static boolean existsByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE Email = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
