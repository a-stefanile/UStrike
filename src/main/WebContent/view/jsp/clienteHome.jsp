<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String ctx = request.getContextPath();
    String nome = (String) session.getAttribute("nomeUtente");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>UStrike - Home Cliente</title>
</head>
<body>

<h2>Home Cliente</h2>

<p>
    Benvenuto<%= (nome != null ? ", " + nome : "") %>
</p>

<ul>
    <li>
        <a href="<%= ctx %>/cliente/crea-prenotazione">Nuova prenotazione</a>
    </li>
    <li>
        <a href="<%= ctx %>/cliente/prenotazioni">Le mie prenotazioni</a>
    </li>
    <li>
        <a href="<%= ctx %>/logout">Logout</a>
    </li>
</ul>

</body>
</html>
