package com.ustrike.control;

import static org.mockito.Mockito.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

class ClienteHomeServletUnitTest {

    private ClienteHomeServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        // Inizializza i mock
        MockitoAnnotations.openMocks(this);
        
        // Istanza della servlet
        servlet = new ClienteHomeServlet();
        
        // Configura il mock del dispatcher per la vista specifica della home cliente
        when(request.getRequestDispatcher("/view/jsp/clienteHome.jsp")).thenReturn(dispatcher);
    }

    @Test
    void testDoGet_EsegueForwardCorretto() throws ServletException, IOException {
        // 1. Arrange: Simuliamo una richiesta GET
        when(request.getMethod()).thenReturn("GET");

        // 2. Act: Eseguiamo tramite il metodo service()
        servlet.service(request, response);

        // 3. Assert: Verifichiamo che venga chiamato il dispatcher corretto e il forward
        verify(request).getRequestDispatcher("/view/jsp/clienteHome.jsp");
        verify(dispatcher).forward(request, response);
    }
}