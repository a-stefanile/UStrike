package com.ustrike.control;

import com.ustrike.model.dto.Cliente;
import com.ustrike.model.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegistrazioneServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW_REGISTER = "/view/jsp/register.jsp";
    private static final String VIEW_LOGIN    = "/view/jsp/login.jsp";

    // Password: >=8, maiusc, minusc, numero, speciali 
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome        = request.getParameter("nome").trim();
        String cognome     = request.getParameter("cognome").trim();
        String email       = request.getParameter("email").trim().toLowerCase();
        String password    = request.getParameter("password");
        String confPassword= request.getParameter("confPassword");

        
        StringBuilder errors = new StringBuilder();
        if (nome.length() < 2)                  errors.append("Nome: min 2 caratteri. ");
        if (cognome.length() < 2)               errors.append("Cognome: min 2 caratteri. ");
        if (!EMAIL_PATTERN.matcher(email).matches())
                                              errors.append("Email non valida. ");
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches())
                                              errors.append("Password: deve avere almeno 8 caratteri di cui almeno: 1 maiusc/1 minusc/1 numero. ");
        if (!password.equals(confPassword))     errors.append("Password non corrispondono. ");

        if (errors.length() > 0) {
            request.setAttribute("errorMessage", errors.toString());
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
            return;
        }

        try {
            Cliente cliente = new Cliente();
            cliente.setNomeCliente(nome);
            cliente.setCognomeCliente(cognome);
            cliente.setEmail(email);
            cliente.setPuntiTicket(0);

            // ✅ UserService gestisce hash + unique check
            boolean creato = userService.createCliente(cliente, password);

            if (creato) {
                request.setAttribute("successMessage", 
                    String.format("Account creato! Benvenuto %s %s. Effettua login.", nome, cognome));
                request.getRequestDispatcher(VIEW_LOGIN).forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Email già registrata. Usa login.");
                request.setAttribute("nome", nome);
                request.setAttribute("cognome", cognome);
                request.setAttribute("email", email);
                request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Errore database. Riprova.");
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
        }
    }
}
