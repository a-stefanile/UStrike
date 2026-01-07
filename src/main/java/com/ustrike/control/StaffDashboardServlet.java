package com.ustrike.control;

import com.ustrike.model.service.PrenotazioneService;
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
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PrenotazioneService service = new PrenotazioneService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Filtro già verifica "staff"
        String filter = request.getParameter("filter");
        if (filter == null) filter = "inAttesa";
        
        List<Prenotazione> prenotazioni;
        if ("inAttesa".equals(filter)) {
            prenotazioni = service.getCatalogoInAttesa();
        } else {
            prenotazioni = service.getCatalogoCompletate();
        }
        
        request.setAttribute("prenotazioni", prenotazioni);
        request.setAttribute("filter", filter);
        request.getRequestDispatcher("/staff/catalogo-prenotazioni.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");
        int idPrenotazione = Integer.parseInt(request.getParameter("idPrenotazione"));
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            boolean success;
            switch (action) {
                case "accetta":
                    success = service.accettaPrenotazione(idPrenotazione, userId);
                    break;
                case "rifiuta":
                    String motivo = request.getParameter("motivo");
                    success = service.rifiutaPrenotazione(idPrenotazione, userId, motivo);
                    break;
                default:
                    out.print("{\"success\":false,\"error\":\"Action invalida\"}");
                    return;
            }
            out.print("{\"success\":" + success + "}");
        } catch (Exception e) {
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}
