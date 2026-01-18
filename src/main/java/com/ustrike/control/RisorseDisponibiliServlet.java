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
    //private final RisorsaService risorsaService = new RisorsaService();

    private RisorsaService risorsaService;
    
    public RisorseDisponibiliServlet() {
    	this.risorsaService = new RisorsaService();
    }
    
    public RisorseDisponibiliServlet(RisorsaService risorsaService) {
    	this.risorsaService = risorsaService;
    }
    
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
        String dataStr = request.getParameter("data");      
        String orarioStr = request.getParameter("orario");  

        try {
            if (idServizioStr == null || dataStr == null || orarioStr == null ||
                idServizioStr.isBlank() || dataStr.isBlank() || orarioStr.isBlank()) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }

            // Validazione orario (17-23 e 00-02)
            if (!orarioStr.trim().matches("^(1[7-9]|2[0-3]|0[0-2]):00$")) {
                out.print("{\"success\":false,\"error\":\"Orario non valido (17:00-02:00)\"}");
                return;
            }

            int idServizio = Integer.parseInt(idServizioStr.trim());
            LocalDate dataPrenotazione = LocalDate.parse(dataStr.trim());
            LocalTime oraPrenotazione = LocalTime.parse(orarioStr.trim() + ":00");
            
            LocalDateTime ldt;
            if (oraPrenotazione.getHour() >= 0 && oraPrenotazione.getHour() <= 2) {
                ldt = LocalDateTime.of(dataPrenotazione.plusDays(1), oraPrenotazione);
            } else {
                ldt = LocalDateTime.of(dataPrenotazione, oraPrenotazione);
            }
            
            Timestamp tsOrario = Timestamp.valueOf(ldt);

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
                  .append(" (Capacità: ").append(r.getCapacita()).append(")\"}");
                if (i < disponibili.size() - 1) sb.append(",");
            }
            sb.append("]}");

            out.print(sb.toString());
        } catch (Exception e) {
            e.printStackTrace(); // Utile per il debug sul server
            out.print("{\"success\":false,\"error\":\"Errore interno del server\"}");
        } finally {
            out.flush();
        }
    }
}