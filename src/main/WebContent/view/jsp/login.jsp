<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - UStrike</title>
</head>
<body>
<h1>Login</h1>

<c:if test="${not empty errorMessage}">
    <p style="color:red">${errorMessage}</p>
</c:if>
<c:if test="${not empty successMessage}">
    <p style="color:green">${successMessage}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/login">
    <label>Email</label><br>
    <input type="email" name="email" required><br><br>

    <label>Password</label><br>
    <input type="password" name="password" required><br><br>

    <button type="submit">Accedi</button>
</form>

<p>
    Non hai un account?
    <a href="${pageContext.request.contextPath}/register">Registrati</a>
</p>
</body>
</html>
