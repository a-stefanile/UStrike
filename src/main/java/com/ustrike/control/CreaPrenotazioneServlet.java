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
        request.getRequestDispatcher("/cliente/prenotazioni-form.jsp").forward(request, response);
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
        PrintWriter out = response.getWriter();

        try {
            String data = request.getParameter("data");        // es: 2026-01-10
            String orario = request.getParameter("orario");    // es: 19:30
            String partecipanti = request.getParameter("partecipanti");
            String idServizioStr = request.getParameter("idServizio");
            String idRisorsaStr = request.getParameter("idRisorsa");

            if (data == null || orario == null || idServizioStr == null || idRisorsaStr == null) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }
            if (partecipanti == null || partecipanti.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Partecipanti richiesti\"}");
                return;
            }

            int idServizio = Integer.parseInt(idServizioStr.trim());
            int idRisorsa = Integer.parseInt(idRisorsaStr.trim());

            // Coerente col tuo DB: Data = solo giorno, Orario = timestamp completo giorno+ora
            Timestamp tsData = Timestamp.valueOf(data.trim() + " 00:00:00");
            Timestamp tsOrario = Timestamp.valueOf(data.trim() + " " + orario.trim() + ":00");

            // Controllo disponibilità risorsa per quell'orario
            if (!risorsaService.isRisorsaDisponibile(idRisorsa, tsOrario)) {
                out.print("{\"success\":false,\"error\":\"Risorsa non disponibile\"}");
                return;
            }

            // Il tuo service invalida la cache "prenotazioni_{idCliente}" nella session
            int idPrenotazione = prenotazioneService.creaPrenotazione(
                    tsData, tsOrario, partecipanti.trim(),
                    idServizio, idRisorsa, userId, session
            );

            if (idPrenotazione <= 0) {
                out.print("{\"success\":false,\"error\":\"Creazione fallita\"}");
                return;
            }

            out.print("{\"success\":true,\"idPrenotazione\":" + idPrenotazione + "}");
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"error\":\"Dati numerici non validi\"}");
        } catch (IllegalArgumentException e) {
            // Timestamp.valueOf() lancia IllegalArgumentException se formato errato
            out.print("{\"success\":false,\"error\":\"Data/orario non validi\"}");
        } catch (Exception e) {
            out.print("{\"success\":false,\"error\":\"Errore server\"}");
        } finally {
            out.flush();
        }
    }
}
