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

            // Fasce di 1 ora: HH:00
            if (!orario.trim().matches("^([01]\\d|2[0-3]):00$")) {
                out.print("{\"success\":false,\"error\":\"Orario non valido (solo fasce HH:00)\"}");
                return;
            }

            int idServizio = Integer.parseInt(idServizioStr.trim());
            Timestamp tsOrario = Timestamp.valueOf(data.trim() + " " + orario.trim() + ":00");

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
