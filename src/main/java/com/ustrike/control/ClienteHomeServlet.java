package com.ustrike.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cliente/home")
public class ClienteHomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW_HOME = "/view/jsp/clienteHome.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(VIEW_HOME).forward(request, response);
    }
}
