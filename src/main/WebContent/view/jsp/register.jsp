<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registrazione - UStrike</title>
</head>
<body>
<h1>Registrazione</h1>

<c:if test="${not empty errorMessage}">
    <p style="color:red">${errorMessage}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/register">
    <label>Nome</label><br>
    <input type="text" name="nome" value="${nome}" required><br><br>

    <label>Cognome</label><br>
    <input type="text" name="cognome" value="${cognome}" required><br><br>

    <label>Email</label><br>
    <input type="email" name="email" value="${email}" required><br><br>

    <label>Password</label><br>
    <input type="password" name="password" required><br><br>

    <label>Conferma password</label><br>
    <input type="password" name="confPassword" required><br><br>

    <button type="submit">Crea account</button>
</form>

<p>
    Hai già un account?
    <a href="${pageContext.request.contextPath}/login">Login</a>
</p>
</body>
</html>
