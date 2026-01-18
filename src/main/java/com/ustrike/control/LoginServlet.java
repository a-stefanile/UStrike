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

    
    private static final String VIEW_LOGIN = "/view/jsp/login.jsp";
    private static final String ERROR_ACCESSO_NEGATO = "accesso-negato";

    private UserService userService;
/*
    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }
*/
    
    public LoginServlet() {
    	this.userService = new  UserService();
    }
    
    public LoginServlet(UserService userService) {
    	this.userService = userService;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String error = request.getParameter("error");
        if (ERROR_ACCESSO_NEGATO.equals(error)) {
            request.setAttribute("errorMessage",
                    "Accesso negato. Effettua login con credenziali corrette.");
        }

        request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String emailRaw = request.getParameter("email");
        String password = request.getParameter("password");

        String email = (emailRaw == null) ? "" : emailRaw.trim().toLowerCase();

        
        if (email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Email e password obbligatorie.");
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            return;
        }

        try {
            // 3) Autenticazione
            Object authResult = userService.authenticateUser(email, password);

            if (authResult == null) {
                request.setAttribute("errorMessage", "Email o password errate. Riprova.");
                request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
                return;
            }

            // 4) Lettura risultato
            Object[] arr = (Object[]) authResult;
            String ruolo = (String) arr[0];     
            int userId = (Integer) arr[1];      
            String nome = (String) arr[2];      

            
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = request.getSession(true);

            session.setAttribute("ruolo", ruolo);
            session.setAttribute("userId", userId);
            session.setAttribute("nomeUtente", nome);

            System.out.printf("[LOGIN OK] %s (ID:%d, ruolo:%s) alle %tc%n",
                    nome, userId, ruolo, new java.util.Date());

            // 6) Redirect role-based
            String ctx = request.getContextPath();
            switch (ruolo) {
                case "cliente":
                    response.sendRedirect(ctx + "/cliente/home");
                    break;

                case "staff":
                    
                    response.sendRedirect(ctx + "/staff/catalogo");
                    break;

                case "manager":
                    response.sendRedirect(ctx + "/manager/dashboard");
                    break;

                default:
                    throw new IllegalStateException("Ruolo non gestito: " + ruolo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage",
                    "Servizio temporaneamente non disponibile. Riprova.");
            request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
        }
    }
}
