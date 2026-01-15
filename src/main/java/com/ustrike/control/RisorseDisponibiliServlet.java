package com.ustrike.control;

import com.ustrike.model.dto.Risorsa;
import com.ustrike.model.service.RisorsaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cliente/risorse-disponibili")
public class RisorseDisponibiliServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final RisorsaService risorsaService = new RisorsaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"cliente".equals(session.getAttribute("ruolo"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idServizioStr = request.getParameter("idServizio");
        String data = request.getParameter("data");      // YYYY-MM-DD
        String orario = request.getParameter("orario");  // HH:00
        

        try {
            if (idServizioStr == null || data == null || orario == null ||
                idServizioStr.isBlank() || data.isBlank() || orario.isBlank()) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }

         // Fasce consentite: 17-23 e 00-02
            if (!orario.trim().matches("^(1[7-9]|2[0-3]|0[0-2]):00$")) {
                out.print("{\"success\":false,\"error\":\"Orario non valido (17:00-02:00)\"}");
                return;
            }


            int idServizio = Integer.parseInt(idServizioStr.trim());
            String fascia = orario.trim() + ":00"; // "14:00:00"
            Timestamp tsOrario;
            if (orario.startsWith("0")) {
                // ore 00-02 → giorno successivo
                LocalDate dataPrenotazione = LocalDate.parse(data.trim());
                LocalTime ora = LocalTime.parse(orario.trim() + ":00");
                tsOrario = Timestamp.valueOf(LocalDateTime.of(dataPrenotazione.plusDays(1), ora));
            } else {
                tsOrario = Timestamp.valueOf(data.trim() + " " + orario.trim() + ":00");
            }


            List<Risorsa> candidate = risorsaService.getRisorseLibereByServizio(idServizio);

            List<Risorsa> disponibili = new ArrayList<>();
            for (Risorsa r : candidate) {
                if (risorsaService.isRisorsaDisponibile(r.getIDRisorsa(), tsOrario)) {
                    disponibili.add(r);
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{\"success\":true,\"risorse\":[");
            for (int i = 0; i < disponibili.size(); i++) {
                Risorsa r = disponibili.get(i);
                sb.append("{\"id\":").append(r.getIDRisorsa())
                  .append(",\"label\":\"Risorsa ").append(r.getIDRisorsa())
                  .append(" (cap ").append(r.getCapacita()).append(")\"}");
                if (i < disponibili.size() - 1) sb.append(",");
            }
            sb.append("]}");

            out.print(sb.toString());
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"error\":\"IDServizio non valido\"}");
        } catch (IllegalArgumentException e) {
            out.print("{\"success\":false,\"error\":\"Data/orario non validi\"}");
        } catch (Exception e) {
            out.print("{\"success\":false,\"error\":\"Errore server\"}");
        } finally {
            out.flush();
        }
    }
}
