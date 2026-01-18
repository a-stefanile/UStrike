package com.ustrike.control;

import com.ustrike.model.service.PrenotazioneService;
import com.ustrike.model.service.RisorsaService;
import com.ustrike.model.service.ServizioService;
import com.ustrike.model.dto.Servizio;
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
import java.util.List;

@WebServlet("/cliente/crea-prenotazione")
public class CreaPrenotazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    //private final PrenotazioneService prenotazioneService = new PrenotazioneService();
    //private final ServizioService servizioService = new ServizioService();
    //private final RisorsaService risorsaService = new RisorsaService();
	private PrenotazioneService prenotazioneService;
	private ServizioService servizioService;
	private RisorsaService risorsaService;
	
    public CreaPrenotazioneServlet() {
    	this.prenotazioneService = new PrenotazioneService();
    	this.servizioService = new ServizioService();
    	this.risorsaService = new RisorsaService();
    }
    
    public CreaPrenotazioneServlet(PrenotazioneService p, ServizioService s, RisorsaService r) {
    	this.prenotazioneService = p;
    	this.servizioService = s;
    	this.risorsaService =r;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo"); // bowling | kart
        Integer idServizioSelezionato = null;
        List<Servizio> serviziAbilitati = servizioService.getServiziAbilitati();

        if (tipo != null) {
            for (Servizio s : serviziAbilitati) {
                if (tipo.equalsIgnoreCase("bowling") && s.getNomeServizio().equalsIgnoreCase("Bowling")) {
                    idServizioSelezionato = s.getIDServizio();
                } else if (tipo.equalsIgnoreCase("kart") && (s.getNomeServizio().equalsIgnoreCase("Go-Kart") || s.getNomeServizio().equalsIgnoreCase("GoKart"))) {
                    idServizioSelezionato = s.getIDServizio();
                }
            }
        }

        request.setAttribute("tipo", tipo); 
        request.setAttribute("idServizioSelezionato", idServizioSelezionato);
        request.setAttribute("servizi", serviziAbilitati);

        // Assicurati che il percorso della JSP sia corretto rispetto alla tua cartella WEB-INF o WebContent
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
            String dataStr = request.getParameter("data");
            String orarioStr = request.getParameter("orario"); 
            String idServizioStr = request.getParameter("idServizio");
            String idRisorsaStr = request.getParameter("idRisorsa");
            String numPartecipantiStr = request.getParameter("numPartecipanti");

            if (isBlank(dataStr) || isBlank(orarioStr) || isBlank(idServizioStr) || isBlank(idRisorsaStr) || isBlank(numPartecipantiStr)) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }

            int idServizio = Integer.parseInt(idServizioStr.trim());
            int idRisorsa = Integer.parseInt(idRisorsaStr.trim());
            int numPartecipanti = Integer.parseInt(numPartecipantiStr.trim());

            // Calcolo Timestamp coerente (gestione mezzanotte)
            LocalDate dataPren = LocalDate.parse(dataStr.trim());
            LocalTime oraPren = LocalTime.parse(orarioStr.trim() + ":00");
            LocalDateTime ldt;
            if (oraPren.getHour() >= 0 && oraPren.getHour() <= 2) {
                ldt = LocalDateTime.of(dataPren.plusDays(1), oraPren);
            } else {
                ldt = LocalDateTime.of(dataPren, oraPren);
            }
            Timestamp tsOrario = Timestamp.valueOf(ldt);
            Timestamp tsData = Timestamp.valueOf(dataPren.atStartOfDay());

            if (!risorsaService.isRisorsaDisponibile(idRisorsa, tsOrario)) {
                out.print("{\"success\":false,\"error\":\"Risorsa non più disponibile per l'orario scelto\"}");
                return;
            }

            // Recupero nomi partecipanti
            StringBuilder partecipantiSb = new StringBuilder();
            for (int i = 1; i <= numPartecipanti; i++) {
                String nome = request.getParameter("partecipante" + i);
                if (nome != null && !nome.isBlank()) {
                    if (partecipantiSb.length() > 0) partecipantiSb.append(", ");
                    partecipantiSb.append(nome.trim());
                }
            }
            String partecipanti = partecipantiSb.toString();

            int idPrenotazione = prenotazioneService.creaPrenotazione(
                    tsData, tsOrario, partecipanti,
                    idServizio, idRisorsa, userId, session
            );

            if (idPrenotazione > 0) {
                out.print("{\"success\":true,\"message\":\"Richiesta inviata con successo!\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Errore durante il salvataggio della prenotazione\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("{\"success\":false,\"error\":\"Errore server\"}");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}