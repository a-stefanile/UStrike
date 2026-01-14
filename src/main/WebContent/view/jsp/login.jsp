<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - UStrike</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body>
	<!-- Logo -->
	<div class="logo">
	    <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
	</div>
	
    <form method="post" action="${pageContext.request.contextPath}/login">
        <h1>Accedi</h1>
        
        <c:if test="${not empty errorMessage}">
            <p class="error">${errorMessage}</p>
        </c:if>
        <c:if test="${not empty successMessage}">
            <p class="success">${successMessage}</p>
        </c:if>
        
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>
        
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>
        
        <button type="submit">Accedi</button>
    </form>
    
    <p class="register-link">
        Non hai un account?
        <a href="${pageContext.request.contextPath}/view/jsp/register.jsp">Registrati</a>
    </p>
</body>
</html>
