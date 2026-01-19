package com.ustrike.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.ustrike.model.service.PrenotazioneService;

@WebServlet("/cliente/annulla-prenotazione")
public class AnnullaPrenotazioneServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PrenotazioneService service = new PrenotazioneService();
	
	public AnnullaPrenotazioneServlet() {
		this.service = new PrenotazioneService();
	}
	
	public AnnullaPrenotazioneServlet(PrenotazioneService service) {
		this.service = service;
	}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        int idCliente = userIdObj;

        String idStr = request.getParameter("idPrenotazione");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int idPrenotazione = Integer.parseInt(idStr.trim());
        service.annullaPrenotazioneCliente(idPrenotazione, idCliente, session);
        
        session.setAttribute("flashMsg", "Prenotazione annullata con successo.");

        // redirect dopo POST (PRG)
        response.sendRedirect(request.getContextPath() + "/cliente/prenotazioni");
    }
}
