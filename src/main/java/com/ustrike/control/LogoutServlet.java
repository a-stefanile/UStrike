package com.ustrike.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW_INDEX = "/index.jsp";  // O /login.jsp se preferisci

    /**
     * PRE: Sessione esistente (o no) → POST: Sessione invalidata + redirect index
     * Invariante: Logout SEMPRE possibile, anche da stato inconsistente
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }

    private void performLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);  // Non crea se non esiste
        
        if (session != null) {
            // 🔒 Log per audit (compatibile col tuo filtro)
            String ruolo = (String) session.getAttribute("ruolo");
            String nome = (String) session.getAttribute("nomeUtente");
            Integer userId = (Integer) session.getAttribute("userId");
            
            System.out.printf("[LOGOUT] %s (ID:%d, ruolo:%s) alle %tc%n", 
                            nome != null ? nome : "anonimo", 
                            userId != null ? userId : -1, 
                            ruolo != null ? ruolo : "nessuno",
                            new java.util.Date());
            
            // 🧹 Pulizia TOTALE sessione (post-cond: nessun dato residuo)
            session.invalidate();
        }
        
        // 🚀 Redirect index (post-cond)
        response.sendRedirect(request.getContextPath() + VIEW_INDEX);
    }
}