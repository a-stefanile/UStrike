package com.ustrike.control;

import com.ustrike.model.dto.Prenotazione;
import com.ustrike.model.dto.PrenotazioneView;
import com.ustrike.model.service.PrenotazioneService;
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
    private static final long serialVersionUID = 1L;

    private final PrenotazioneService service = new PrenotazioneService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        List<PrenotazioneView> prenotazioni = service.getPrenotazioniClienteView(userId, session);
        request.setAttribute("prenotazioni", prenotazioni);
        request.getRequestDispatcher("/view/jsp/mie-prenotazioni.jsp").forward(request, response);

    }

}
        

