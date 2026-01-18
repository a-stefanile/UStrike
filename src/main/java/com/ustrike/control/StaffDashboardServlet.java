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

    private  CatalogoPrenotazioni catalogo;
    private  PrenotazioneDAO dao;

    public StaffDashboardServlet() {
    	super();
    	this.catalogo = CatalogoPrenotazioni.getInstance();
    	this.dao = new PrenotazioneDAO();
    }
    
    public StaffDashboardServlet(CatalogoPrenotazioni catalogo, PrenotazioneDAO dao) {
    	this.catalogo = catalogo;
    	this.dao = dao;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String filter = request.getParameter("filter");
        if (filter == null) filter = "In attesa";

        List<Prenotazione> prenotazioni;
        switch (filter) {
            case "In attesa":
                prenotazioni = catalogo.getPrenotazioniInAttesa();
                break;
            case "Completate":
                prenotazioni = catalogo.getPrenotazioniCompletate();
                break;
            case "all":
                prenotazioni = catalogo.getTuttePrenotazioni();
                break;
            default:
                filter = "In attesa";
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

        String action = request.getParameter("action");
        String idStr = request.getParameter("idPrenotazione");

        try (PrintWriter out = response.getWriter()) {

            if (action == null || idStr == null || idStr.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Parametri mancanti\"}");
                return;
            }

            int idPrenotazione = Integer.parseInt(idStr.trim());

            boolean success;
            String notaStaff = null;

            if ("rifiuta".equals(action)) {
                // motivo scelto dal menu a tendina (campo 'motivo' inviato dal JS)
                String motivo = request.getParameter("motivo"); // lettura parametri standard [web:128]
                if (motivo == null || motivo.trim().isEmpty()) {
                    out.print("{\"success\":false,\"error\":\"Motivo rifiuto mancante\"}");
                    return;
                }
                notaStaff = motivo.trim();
            }

            switch (action) {
                case "accetta":
                    success = dao.updateStatoPrenotazione(idPrenotazione, "Confermata", staffId, null);
                    break;

                case "rifiuta":
                    success = dao.updateStatoPrenotazione(idPrenotazione, "Rifiutata", staffId, notaStaff);
                    break;

                default:
                    out.print("{\"success\":false,\"error\":\"Action non valida\"}");
                    return;
            }

            // Se update ok, invalida cache lato cliente in sessione (se la stai usando)
            if (success) {
                Prenotazione p = dao.selectPrenotazione(idPrenotazione);
                if (p != null) {
                    session.removeAttribute("prenotazioni_" + p.getIDCliente());
                    session.removeAttribute("prenotazioni_view_" + p.getIDCliente());
                }
            }

            out.print("{\"success\":" + success + "}");
        } catch (NumberFormatException e) {
            response.getWriter().print("{\"success\":false,\"error\":\"ID prenotazione non valido\"}");
        } catch (Exception e) {
            response.getWriter().print("{\"success\":false,\"error\":\"Errore server\"}");
        }
    }
}
