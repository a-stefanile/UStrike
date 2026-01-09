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
import java.util.List;

@WebServlet("/cliente/prenotazioni")
public class MiePrenotazioniServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final PrenotazioneService service = new PrenotazioneService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");
        
        
        List<Prenotazione> prenotazioni = service.getPrenotazioniCliente(userId, session);
        request.setAttribute("prenotazioni", prenotazioni);
        request.getRequestDispatcher("/cliente/mie-prenotazioni.jsp").forward(request, response);
    }
}
