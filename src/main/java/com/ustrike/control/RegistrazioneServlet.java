package com.ustrike.control;

import com.ustrike.model.dto.Cliente;
import com.ustrike.model.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegistrazioneServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW_REGISTER = "/jsp/register.jsp";
    private static final String VIEW_LOGIN    = "/jsp/login.jsp";

    // ✅ Password: >=8, maiusc, minusc, numero
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    /** Mostra form registrazione (pubblico) */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
    }

    /** 
     * PRE: nome,cognome,email,password,confPassword presenti; email unica; pwd>=8+complex
     * POST: Cliente creato con ruolo CLIENTE; redirect login.jsp + successo
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔍 Parametri (pre-cond)
        String nome        = request.getParameter("nome").trim();
        String cognome     = request.getParameter("cognome").trim();
        String email       = request.getParameter("email").trim().toLowerCase();
        String password    = request.getParameter("password");
        String confPassword= request.getParameter("confPassword");

        // ── VALIDAZIONE RIGIDA (pre-cond) ───────────────────────────────────
        StringBuilder errors = new StringBuilder();

        if (nome.length() < 2)                  errors.append("Nome: min 2 caratteri. ");
        if (cognome.length() < 2)               errors.append("Cognome: min 2 caratteri. ");
        if (!EMAIL_PATTERN.matcher(email).matches())
                                              errors.append("Email formato non valido. ");
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches())
                                              errors.append("Password: min 8 char, 1 maiusc, 1 minusc, 1 numero. ");
        if (!password.equals(confPassword))     errors.append("Conferma password non corrisponde. ");

        if (errors.length() > 0) {
            request.setAttribute("errorMessage", errors.toString());
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
            return;
        }

        // ── CREAZIONE CLIENTE (invariante: email unica + hash) ──────────────
        try {
            Cliente cliente = new Cliente();
            cliente.setNomeCliente(nome);
            cliente.setCognomeCliente(cognome);
            cliente.setEmail(email);
            cliente.setPasswordHash(password);  // ← Service farà hash!
            cliente.setPuntiTicket(0);         // Default per nuovi clienti

            boolean creato = userService.createUser(cliente);  // ✅ Verifica unicità + hash + save

            if (creato) {
                // POST: Utente creato → Redirect login + messaggio
                request.setAttribute("successMessage", 
                    String.format("Account creato! Benvenuto %s. Effettua login.", 
                                cliente.getFullName()));
                
                // 👉 EMAIL CONFERMA (opzionale - implementa se hai EmailService)
                // sendConfirmationEmail(email, nome);
                
                request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            } else {
                // Service ha rilevato email DUPLICATA (invariante)
                request.setAttribute("errorMessage", "Email già registrata. Usa 'Password dimenticata o login.");
                request.setAttribute("nome", nome);
                request.setAttribute("cognome", cognome);
                request.setAttribute("email", email);
                request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore server. Riprova.");
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
        }
    }
}