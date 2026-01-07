package com.ustrike.control;

import com.ustrike.model.service.PrenotazioneService;
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
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PrenotazioneService service = new PrenotazioneService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/cliente/prenotazioni-form.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            Timestamp data = Timestamp.valueOf(request.getParameter("data") + " 00:00:00");
            Timestamp orario = Timestamp.valueOf("1970-01-01 " + request.getParameter("fascia") + ":00");
            String partecipanti = request.getParameter("partecipanti");
            int idServizio = Integer.parseInt(request.getParameter("idServizio"));
            
            service.creaPrenotazione(data, orario, partecipanti, idServizio, 0, userId);
            out.print("{\"success\":true}");
        } catch (Exception e) {
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}
