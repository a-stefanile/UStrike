package com.ustrike.control;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ustrike.model.service.UserService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class LoginServletUnitTest {

    private LoginServlet servlet;

    @Mock private UserService userServiceMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Utilizziamo il costruttore per iniettare il mock del service
        servlet = new LoginServlet(userServiceMock);
        
        // Setup dei comportamenti comuni
        when(request.getContextPath()).thenReturn("/UStrike");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testLogin_SuccessoCliente() throws Exception {
        // Arrange
        preparaPostRequest("mario@test.it", "password123");
        Object[] authResult = new Object[]{"cliente", 10, "Mario Rossi"};
        when(userServiceMock.authenticateUser("mario@test.it", "password123")).thenReturn(authResult);
        when(request.getSession(true)).thenReturn(session);

        // Act
        servlet.service(request, response);

        // Assert
        verify(session).setAttribute("ruolo", "cliente");
        verify(session).setAttribute("userId", 10);
        verify(response).sendRedirect("/UStrike/cliente/home");
    }

    @Test
    void testLogin_SuccessoStaff() throws Exception {
        // Arrange
        preparaPostRequest("staff@test.it", "staffPass!");
        Object[] authResult = new Object[]{"staff", 20, "Luigi Bianchi"};
        when(userServiceMock.authenticateUser("staff@test.it", "staffPass!")).thenReturn(authResult);
        when(request.getSession(true)).thenReturn(session);

        // Act
        servlet.service(request, response);

        // Assert
        verify(session).setAttribute("ruolo", "staff");
        verify(response).sendRedirect("/UStrike/staff/catalogo");
    }

    @Test
    void testLogin_SuccessoManager() throws Exception {
        // Arrange
        preparaPostRequest("manager@test.it", "admin123");
        Object[] authResult = new Object[]{"manager", 1, "Il Capo"};
        when(userServiceMock.authenticateUser("manager@test.it", "admin123")).thenReturn(authResult);
        when(request.getSession(true)).thenReturn(session);

        // Act
        servlet.service(request, response);

        // Assert
        verify(session).setAttribute("ruolo", "manager");
        verify(response).sendRedirect("/UStrike/manager/dashboard");
    }

    @Test
    void testLogin_CredenzialiErrate() throws Exception {
        // Arrange
        preparaPostRequest("sconosciuto@test.it", "wrong");
        when(userServiceMock.authenticateUser(anyString(), anyString())).thenReturn(null);

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("errate"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testLogin_CampiObbligatoriMancanti() throws Exception {
        // Arrange
        preparaPostRequest("", ""); // Email e password vuote

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("obbligatorie"));
        verify(dispatcher).forward(request, response);
        // Il service non deve essere nemmeno chiamato se i campi sono vuoti
        verifyNoInteractions(userServiceMock);
    }

    @Test
    void testDoGet_MessaggioAccessoNegato() throws Exception {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("error")).thenReturn("accesso-negato");

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("Accesso negato"));
        verify(dispatcher).forward(request, response);
    }

    // Metodo di utility per pulire il codice dei test POST
    private void preparaPostRequest(String email, String password) {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
    }
}