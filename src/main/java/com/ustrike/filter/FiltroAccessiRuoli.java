package com.ustrike.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FiltroAccessiRuoli:
 * Gestisce l'accesso alle aree riservate in base al ruolo:
 *  - cliente  -> /cliente/*, /jsp/cliente/*
 *  - staff    -> /staff/*,   /jsp/staff/*
 *  - manager  -> /manager/*, /jsp/manager/*
 *
 * Si aspetta in sessione un attributo "ruolo" con uno di questi valori.
 */
@WebFilter(urlPatterns = {
        "/cliente/*",      // area cliente
        "/staff/*",        // area staff
        "/manager/*",      // area manager
        "/jsp/cliente/*",
        "/jsp/staff/*",
        "/jsp/manager/*"
})
public class FiltroAccessiRuoli implements Filter {

    private static final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. (Opzionale) Forza HTTPS solo in produzione
        if (!req.isSecure() && "production".equals(System.getenv("MODE"))) {
            String query = req.getQueryString();
            String secureUrl = "https://" + req.getServerName()
                    + req.getRequestURI()
                    + (query != null ? "?" + query : "");
            res.sendRedirect(secureUrl);
            return;
        }

        // 2. Recupera il ruolo dalla sessione
        HttpSession session = req.getSession(false);
        String ruolo = null;
        if (session != null) {
            Object ruoloObj = session.getAttribute("ruolo"); // "cliente", "staff", "manager"
            if (ruoloObj instanceof String) {
                ruolo = (String) ruoloObj;
            }
        }

        String path = req.getServletPath(); // es: /cliente/home, /staff/turni, /manager/dashboard

        // 3. Area Cliente: solo cliente
        if (path.startsWith("/cliente") || path.startsWith("/jsp/cliente")) {
            if (!"cliente".equals(ruolo)) {
                negaAccesso(req, res);
                return;
            }
        }

        // 4. Area Staff: solo staff
        if (path.startsWith("/staff") || path.startsWith("/jsp/staff")) {
            if (!"staff".equals(ruolo)) {
                negaAccesso(req, res);
                return;
            }
        }

        // 5. Area Manager: solo manager
        if (path.startsWith("/manager") || path.startsWith("/jsp/manager")) {
            if (!"manager".equals(ruolo)) {
                negaAccesso(req, res);
                return;
            }
        }

        // 6. continua
        chain.doFilter(request, response);
    }

    private void negaAccesso(HttpServletRequest req, HttpServletResponse res) throws IOException {
        System.out.printf("[%s] ACCESSO NEGATO a %s (ruolo mancante o non autorizzato)%n",
                LocalTime.now().format(LOG_FMT), req.getRequestURI());
        res.sendRedirect(req.getContextPath() + "/login?error=accesso-negato");
    }
}
