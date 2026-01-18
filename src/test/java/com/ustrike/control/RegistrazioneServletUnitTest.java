package com.ustrike.control;

import static org.mockito.ArgumentMatchers.any;
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

import com.ustrike.model.dto.Cliente;
import com.ustrike.model.service.UserService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class RegistrazioneServletUnitTest {

    private RegistrazioneServlet servlet;

    @Mock private UserService userServiceMock;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Costruttore con parametri per iniettare il mock
        servlet = new RegistrazioneServlet(userServiceMock);
        
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testRegistrazione_Successo() throws Exception {
        // Arrange
        preparaParametri("Mario", "Rossi", "mario@test.it", "Password123!", "Password123!");
        when(userServiceMock.createCliente(any(Cliente.class), eq("Password123!"))).thenReturn(true);

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("successMessage"), contains("Account creato"));
        verify(request).getRequestDispatcher("/view/jsp/login.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testRegistrazione_PasswordNonCorrispondenti() throws Exception {
        // Arrange
        preparaParametri("Mario", "Rossi", "mario@test.it", "Password123!", "Sbagliata123!");

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("Password non corrispondono"));
        // Verifichiamo che i dati inseriti tornino alla pagina per non doverli riscrivere
        verify(request).setAttribute("nome", "Mario");
        verify(request).getRequestDispatcher("/view/jsp/register.jsp");
        // Il service non deve essere chiamato se la validazione fallisce
        verifyNoInteractions(userServiceMock);
    }

    @Test
    void testRegistrazione_EmailGiaEsistente() throws Exception {
        // Arrange
        preparaParametri("Mario", "Rossi", "esistente@test.it", "Password123!", "Password123!");
        // Il service restituisce false se l'email è duplicata
        when(userServiceMock.createCliente(any(Cliente.class), anyString())).thenReturn(false);

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("registrata"));
        verify(request).getRequestDispatcher("/view/jsp/register.jsp");
    }

    @Test
    void testRegistrazione_PasswordTroppoDebole() throws Exception {
        // Arrange
        preparaParametri("Mario", "Rossi", "mario@test.it", "123", "123");

        // Act
        servlet.service(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("almeno 8 caratteri"));
        verifyNoInteractions(userServiceMock);
    }

    private void preparaParametri(String nome, String cognome, String email, String pwd, String conf) {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("nome")).thenReturn(nome);
        when(request.getParameter("cognome")).thenReturn(cognome);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(pwd);
        when(request.getParameter("confPassword")).thenReturn(conf);
    }
}