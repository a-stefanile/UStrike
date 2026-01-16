package com.ustrike.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/biliardo")
public class BiliardoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Recuperiamo la sessione esistente (senza crearne una nuova)
        HttpSession session = request.getSession(false);
        
        // Default: se non loggato, la home è quella generale
        String homeUrl = request.getContextPath() + "/view/jsp/homeGenerale.jsp"; 

        if (session != null) {
            String ruolo = (String) session.getAttribute("ruolo");
            // Se l'utente è un cliente loggato, cambiamo la destinazione
            if ("cliente".equals(ruolo)) {
                homeUrl = request.getContextPath() + "/cliente/home";
            }
            // Qui potresti aggiungere altri else if per manager o staff se necessario
        }

        // Passiamo l'URL alla JSP come attributo della richiesta
        request.setAttribute("homeUrl", homeUrl);

        // Forward alla pagina JSP
        request.getRequestDispatcher("/view/jsp/biliardo.jsp").forward(request, response);
    }
}