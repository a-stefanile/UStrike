package com.ustrike.control;

import com.ustrike.model.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW_LOGIN    = "/jsp/login.jsp";
    private static final String ERROR_ACCESSO_NEGATO = "accesso-negato";

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    /** Mostra form login (pubblico, NON protetto dal filtro) */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Gestisce redirect dal filtro con errore accesso negato
        String error = request.getParameter("error");
        if (ERROR_ACCESSO_NEGATO.equals(error)) {
            request.setAttribute("errorMessage", 
                "Accesso negato. Effettua login con credenziali corrette.");
        }
        
        request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
    }

    /** 
     * PRE: Utente non autenticato, parametri email/password presenti
     * POST: Sessione creata + redirect dashboard O errore + refresh form
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email").trim().toLowerCase();
        String password = request.getParameter("password");

        // ── Validazione base ───────────────────────────────────────────────
        if (email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Email e password obbligatorie.");
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            return;
        }

        // ── AUTENTICAZIONE con UserService ─────────────────────────────────
        try {
            Object authResult = userService.authenticateUser(email, password);
            
            if (authResult != null) {
                // ✅ CAST ESPLICITO → RISOLTO ERRORE COMPILAZIONE
                Object[] resultArray = (Object[]) authResult;
                String ruolo = (String) resultArray[0];   // "cliente", "staff", "manager"
                int userId   = (Integer) resultArray[1];  // ID utente
                String nome  = (String) resultArray[2];   // Nome per welcome
                
                // ✅ Credenziali VALIDE → Crea sessione (invariante: una alla volta)
                HttpSession session = request.getSession(true);
                session.invalidate();  // Pulisce sessioni duplicate
                session = request.getSession(true);  // Nuova sessione pulita
                
                session.setAttribute("ruolo", ruolo);
                session.setAttribute("userId", userId);
                session.setAttribute("nomeUtente", nome);
                
                System.out.printf("[LOGIN OK] %s (ID:%d, ruolo:%s) alle %tc%n", 
                                nome, userId, ruolo, new java.util.Date());

                // 🔄 Role-based redirect (compatibile col filtro)
                String redirectUrl = request.getContextPath();
                switch (ruolo) {
                    case "cliente":
                        redirectUrl += "/cliente/home"; break;
                    case "staff":
                        redirectUrl += "/staff/turni"; break;
                    case "manager":
                        redirectUrl += "/manager/dashboard"; break;
                    default:
                        throw new IllegalStateException("Ruolo non gestito: " + ruolo);
                }
                
                response.sendRedirect(redirectUrl);
                return;
            } else {
                // ❌ Credenziali NON valide
                request.setAttribute("errorMessage", "Email o password errate. Riprova.");
                request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", 
                "Servizio temporaneamente non disponibile. Riprova.");
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
        }
    }
}