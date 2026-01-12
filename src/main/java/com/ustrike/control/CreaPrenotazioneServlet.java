package com.ustrike.control;

import com.ustrike.model.service.PrenotazioneService;
import com.ustrike.model.service.RisorsaService;
import com.ustrike.model.service.ServizioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

@WebServlet("/cliente/crea-prenotazione")
public class CreaPrenotazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PrenotazioneService prenotazioneService = new PrenotazioneService();
    private final ServizioService servizioService = new ServizioService();
    private final RisorsaService risorsaService = new RisorsaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("servizi", servizioService.getServiziAbilitati());
        request.getRequestDispatcher("/view/jsp/prenotazioni-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"cliente".equals(session.getAttribute("ruolo"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Integer userIdObj = (Integer) session.getAttribute("userId");
        if (userIdObj == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int userId = userIdObj;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String data = request.getParameter("data");        // YYYY-MM-DD
            String orario = request.getParameter("orario");    // HH:00
            String partecipanti = request.getParameter("partecipanti");
            String idServizioStr = request.getParameter("idServizio");
            String idRisorsaStr = request.getParameter("idRisorsa");

            if (isBlank(data) || isBlank(orario) || isBlank(idServizioStr) || isBlank(idRisorsaStr)) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }
            if (isBlank(partecipanti)) {
                out.print("{\"success\":false,\"error\":\"Partecipanti richiesti\"}");
                return;
            }

            // Fasce orarie: HH:00
            String fascia = orario.trim();
            if (!fascia.matches("^([01]\\d|2[0-3]):00$")) {
                out.print("{\"success\":false,\"error\":\"Orario non valido (solo fasce HH:00)\"}");
                return;
            }

            int idServizio;
            int idRisorsa;
            try {
                idServizio = Integer.parseInt(idServizioStr.trim());
                idRisorsa = Integer.parseInt(idRisorsaStr.trim());
            } catch (NumberFormatException e) {
                out.print("{\"success\":false,\"error\":\"Dati numerici non validi\"}");
                return;
            }

            Timestamp tsData;
            Timestamp tsOrario;
            try {
                tsData = Timestamp.valueOf(data.trim() + " 00:00:00");
                tsOrario = Timestamp.valueOf(data.trim() + " " + fascia + ":00");
            } catch (IllegalArgumentException e) {
                out.print("{\"success\":false,\"error\":\"Data/orario non validi\"}");
                return;
            }

            if (!risorsaService.isRisorsaDisponibile(idRisorsa, tsOrario)) {
                out.print("{\"success\":false,\"error\":\"Risorsa non disponibile\"}");
                return;
            }

            int idPrenotazione = prenotazioneService.creaPrenotazione(
                    tsData, tsOrario, partecipanti.trim(),
                    idServizio, idRisorsa, userId, session
            );

            if (idPrenotazione <= 0) {
                out.print("{\"success\":false,\"error\":\"Creazione fallita\"}");
                return;
            }

            // SOLUZIONE 1: messaggio in risposta
            out.print("{\"success\":true,"
                    + "\"idPrenotazione\":" + idPrenotazione + ","
                    + "\"message\":\"Richiesta inviata con successo. Attendi la conferma del nostro staff.\""
                    + "}");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
