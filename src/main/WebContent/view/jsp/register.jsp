<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione - UStrike</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/register.css">
</head>
<body>
    <div class="logo">
        <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
    </div>
    
    <form method="post" action="${pageContext.request.contextPath}/register">
        <h1>Registrazione</h1>
    
        <%-- Messaggio di errore inviato dalla Servlet --%>
        <c:if test="${not empty errorMessage}">
            <div style="color:red; margin-bottom: 15px; border: 1px solid red; padding: 10px;">
                ${errorMessage}
            </div>
        </c:if>
        
        <label>Nome</label><br>
        <%-- Aggiunto pattern HTML5: solo lettere, spazi e apostrofi --%>
        <input type="text" name="nome" value="${nome}" 
               pattern="[a-zA-Z\s'àèéìòùÀÈÉÌÒÙ]+" 
               title="Il nome non può contenere numeri o caratteri speciali" 
               required><br><br>

        <label>Cognome</label><br>
        <input type="text" name="cognome" value="${cognome}" 
               pattern="[a-zA-Z\s'àèéìòùÀÈÉÌÒÙ]+" 
               title="Il cognome non può contenere numeri o caratteri speciali" 
               required><br><br>

        <label>Email</label><br>
        <input type="email" name="email" value="${email}" required><br><br>

        <label>Password</label><br>
        <%-- Nota: qui potresti aggiungere il pattern anche per la password se vuoi il check immediato --%>
        <input type="password" name="password" required><br><br>

        <label>Conferma password</label><br>
        <input type="password" name="confPassword" required><br><br>

        <button type="submit">Crea account</button>
    </form>

    <p class="register-link">
        Hai già un account?
        <a href="${pageContext.request.contextPath}/view/jsp/login.jsp">Login</a>
    </p>
</body>
</html>