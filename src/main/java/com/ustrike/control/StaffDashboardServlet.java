package com.ustrike.control;

import com.ustrike.model.CatalogoPrenotazioni;
import com.ustrike.model.dao.PrenotazioneDAO;
import com.ustrike.model.dto.Prenotazione;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/staff/catalogo")
public class StaffDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CatalogoPrenotazioni catalogo = CatalogoPrenotazioni.getInstance();
    private final PrenotazioneDAO dao = new PrenotazioneDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String filter = request.getParameter("filter");
        if (filter == null) filter = "inAttesa";

        List<Prenotazione> prenotazioni;
        switch (filter) {
            case "inAttesa":
                prenotazioni = catalogo.getPrenotazioniInAttesa();
                break;
            case "completate":
                prenotazioni = catalogo.getPrenotazioniCompletate();
                break;
            case "all":
                prenotazioni = catalogo.getTuttePrenotazioni();
                break;
            default:
                filter = "inAttesa";
                prenotazioni = catalogo.getPrenotazioniInAttesa();
        }

        request.setAttribute("prenotazioni", prenotazioni);
        request.setAttribute("filter", filter);
        request.getRequestDispatcher("/view/jsp/catalogo-prenotazioni.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"staff".equals(session.getAttribute("ruolo"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Integer staffIdObj = (Integer) session.getAttribute("userId");
        if (staffIdObj == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int staffId = staffIdObj;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        String idStr = request.getParameter("idPrenotazione");

        try {
            if (action == null || idStr == null || idStr.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }

            int idPrenotazione = Integer.parseInt(idStr.trim());
            boolean success;

            switch (action) {
                case "accetta":
                    success = dao.updateStatoPrenotazione(idPrenotazione, "Confermata", staffId);
                    break;
                case "rifiuta":
                    success = dao.updateStatoPrenotazione(idPrenotazione, "Rifiutata", staffId);
                    break;
                default:
                    out.print("{\"success\":false,\"error\":\"Action non valida\"}");
                    return;
            }

            out.print("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"error\":\"ID prenotazione non valido\"}");
        } catch (Exception e) {
            out.print("{\"success\":false,\"error\":\"Errore server\"}");
        } finally {
            out.flush();
        }
    }
}
