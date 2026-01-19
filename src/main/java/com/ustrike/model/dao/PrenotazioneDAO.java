package com.ustrike.model.dao;

import com.ustrike.model.dto.Prenotazione;
import com.ustrike.model.dto.PrenotazioneView;
import com.ustrike.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {

    public int insertPrenotazione(Timestamp data, Timestamp orario, String stato,
                                  String partecipanti, int idServizio, int idRisorsa,
                                  int idCliente, Integer idStaff, String noteStaff) throws SQLException {

        String sql = "INSERT INTO Prenotazione " +
                "(Data, Orario, StatoPrenotazione, Partecipanti, IDServizio, IDRisorsa, IDCliente, IDStaff, NoteStaff) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, data);
            ps.setTimestamp(2, orario);
            ps.setString(3, stato);
            ps.setString(4, partecipanti);
            ps.setInt(5, idServizio);
            ps.setInt(6, idRisorsa);
            ps.setInt(7, idCliente);

            if (idStaff != null) ps.setInt(8, idStaff);
            else ps.setNull(8, Types.INTEGER); // NULL su FK staff [web:18]

            if (noteStaff != null && !noteStaff.isBlank()) ps.setString(9, noteStaff);
            else ps.setNull(9, Types.VARCHAR); // NULL su testo [web:16][web:23]

            int rows = ps.executeUpdate();
            if (rows <= 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Prenotazione selectPrenotazione(int idPrenotazione) throws SQLException {
        String sql = "SELECT * FROM Prenotazione WHERE IDPrenotazione = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPrenotazione);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToPrenotazione(rs) : null;
            }
        }
    }

    public List<Prenotazione> selectPrenotazioniByCliente(int idCliente) throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione WHERE IDCliente = ? ORDER BY Data DESC, Orario DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) prenotazioni.add(mapResultSetToPrenotazione(rs));
            }
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectPrenotazioniInAttesa() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione WHERE StatoPrenotazione = 'In attesa' ORDER BY Data ASC, Orario ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) prenotazioni.add(mapResultSetToPrenotazione(rs));
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectPrenotazioniCompletate() throws SQLException {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione " +
                     "WHERE StatoPrenotazione IN ('Confermata', 'Rifiutata') " +
                     "ORDER BY Data DESC, Orario DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) prenotazioni.add(mapResultSetToPrenotazione(rs));
        }
        return prenotazioni;
    }

    public List<Prenotazione> selectAllPrenotazioni() throws SQLException {
        List<Prenotazione> tutte = new ArrayList<>();
        String sql = "SELECT * FROM Prenotazione ORDER BY Data DESC, Orario ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) tutte.add(mapResultSetToPrenotazione(rs));
        }
        return tutte;
    }

    /**
     * Aggiorna lo stato, l'ID dello staff che ha gestito la pratica e la nota (motivo).
     * Nota: mantiene la guardia su "In attesa" per evitare doppie lavorazioni.
     */
    public boolean updateStatoPrenotazione(int idPrenotazione, String nuovoStato,
                                          Integer idStaff, String notaStaff) throws SQLException {

        String sql = "UPDATE Prenotazione " +
                     "SET StatoPrenotazione = ?, IDStaff = ?, NoteStaff = ? " +
                     "WHERE IDPrenotazione = ? AND StatoPrenotazione = 'In attesa'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);

            if (idStaff != null) ps.setInt(2, idStaff);
            else ps.setNull(2, Types.INTEGER); // NULL su intero [web:18]

            if (notaStaff != null && !notaStaff.isBlank()) ps.setString(3, notaStaff);
            else ps.setNull(3, Types.VARCHAR); // NULL su testo [web:16][web:23]

            ps.setInt(4, idPrenotazione);

            return ps.executeUpdate() > 0;
        }
    }

    private Prenotazione mapResultSetToPrenotazione(ResultSet rs) throws SQLException {
        // getInt su NULL torna 0, quindi meglio wasNull/getObject [web:22]
        int staffInt = rs.getInt("IDStaff");
        Integer idStaff = rs.wasNull() ? null : staffInt;

        Prenotazione p = new Prenotazione(
                rs.getInt("IDPrenotazione"),
                rs.getTimestamp("Data"),
                rs.getTimestamp("Orario"),
                rs.getString("StatoPrenotazione"),
                rs.getString("Partecipanti"),
                rs.getInt("IDServizio"),
                rs.getInt("IDRisorsa"),
                rs.getInt("IDCliente"),
                idStaff,
                rs.getString("NoteStaff")
        );

        return p;
    }

    public List<PrenotazioneView> selectPrenotazioniByClienteView(int idCliente) throws Exception {
        List<PrenotazioneView> list = new ArrayList<>();

        String sql =
            "SELECT p.IDPrenotazione, p.Data, p.Orario, p.StatoPrenotazione, p.Partecipanti, " +
            "       p.IDServizio, s.NomeServizio, " +
            "       p.IDRisorsa, r.Capacita AS CapacitaRisorsa, " +
            "       p.IDStaff, p.NoteStaff " +
            "FROM Prenotazione p " +
            "INNER JOIN Servizio s ON s.IDServizio = p.IDServizio " +
            "INNER JOIN Risorsa  r ON r.IDRisorsa  = p.IDRisorsa " +
            "WHERE p.IDCliente = ? " +
            "ORDER BY p.Orario DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrenotazioneView v = new PrenotazioneView();
                    v.setIDPrenotazione(rs.getInt("IDPrenotazione"));
                    v.setData(rs.getTimestamp("Data"));
                    v.setOrario(rs.getTimestamp("Orario"));
                    v.setStatoPrenotazione(rs.getString("StatoPrenotazione"));
                    v.setPartecipanti(rs.getString("Partecipanti"));

                    v.setIDServizio(rs.getInt("IDServizio"));
                    v.setNomeServizio(rs.getString("NomeServizio"));

                    v.setIDRisorsa(rs.getInt("IDRisorsa"));
                    v.setCapacitaRisorsa(rs.getInt("CapacitaRisorsa"));

                    int staff = rs.getInt("IDStaff");
                    v.setIDStaff(rs.wasNull() ? null : staff); // gestione NULL [web:22]

                    v.setNoteStaff(rs.getString("NoteStaff"));

                    list.add(v);
                }
            }
        }

        return list;
    }
    
    public boolean doDelete(int idPrenotazione) throws SQLException {
        String sql = "DELETE FROM Prenotazione WHERE IDPrenotazione = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPrenotazione);
            return ps.executeUpdate() > 0;
        }
    }
    
    public boolean annullaPrenotazioneCliente(int idPrenotazione, int idCliente) throws SQLException {
        String sql =
            "UPDATE Prenotazione " +
            "SET StatoPrenotazione = 'Annullata' " +
            "WHERE IDPrenotazione = ? AND IDCliente = ? AND StatoPrenotazione = 'In attesa'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPrenotazione);
            ps.setInt(2, idCliente);

            return ps.executeUpdate() > 0;
        }
    }

}
