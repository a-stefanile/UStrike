<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ustrike.model.dto.Prenotazione" %>

<%
    String ctx = request.getContextPath();
    String filter = (String) request.getAttribute("filter");
    if (filter == null) filter = "In attesa";
    List<Prenotazione> prenotazioni = (List<Prenotazione>) request.getAttribute("prenotazioni");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>UStrike - Dashboard Staff</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="<%= ctx %>/static/css/dashboard.css">

    <style>
        .rifiuto-box { display: none; margin-top: 6px; }
        .rifiuto-box select { max-width: 220px; }
        .rifiuto-box button { margin-left: 6px; }
    </style>
</head>
<body>

<div class="logo">
    <img src="<%= ctx %>/static/images/logo.png" alt="UStrike Logo">
</div>

<div class="page-header">
    <h2>Dashboard Staff - Catalogo Prenotazioni</h2>

    <div class="filter-bar">
        <a href="<%= ctx %>/staff/catalogo?filter=In attesa">In attesa</a>
        <a href="<%= ctx %>/staff/catalogo?filter=Completate">Completate</a>
        <a href="<%= ctx %>/staff/catalogo?filter=all">Tutte</a>
    </div>

    <p class="active-filter">
        Filtro attivo: <b><%= filter %></b>
    </p>
</div>

<div id="msg" class="msg"></div>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Data</th>
        <th>Orario</th>
        <th>Stato</th>
        <th>ID Servizio</th>
        <th>ID Risorsa</th>
        <th>ID Cliente</th>
        <th>ID Staff</th>
        <th>Partecipanti</th>
        <th>Azioni</th>
    </tr>
    </thead>

    <tbody>
    <%
        if (prenotazioni == null || prenotazioni.isEmpty()) {
    %>
        <tr><td colspan="10">Nessuna prenotazione trovata.</td></tr>
    <%
        } else {
            for (Prenotazione p : prenotazioni) {
                int id = p.getIDPrenotazione();
                Integer idStaff = p.getIDStaff();
    %>
        <tr>
            <td><%= id %></td>
            <td><%= p.getData() %></td>
            <td><%= p.getOrario() %></td>
            <td><%= p.getStatoPrenotazione() %></td>
            <td><%= p.getIDServizio() %></td>
            <td><%= p.getIDRisorsa() %></td>
            <td><%= p.getIDCliente() %></td>
            <td><%= (idStaff != null ? idStaff : "-") %></td>
            <td><%= p.getPartecipanti() %></td>

            <td>
                <% if ("In attesa".equalsIgnoreCase(filter)) { %>
                    <button type="button" class="btn-accetta" data-id="<%= id %>">Accetta</button>

                    <button type="button" class="btn-rifiuta" data-id="<%= id %>">Rifiuta</button>

                    <div id="rifiuto-box-<%= id %>" class="rifiuto-box">
                        <select id="motivo-<%= id %>">
                            <option value="">-- Seleziona motivo --</option>
                            <option value="Fascia oraria occupata">Fascia oraria occupata</option>
                            <option value="Giornata non disponibile">Giornata non disponibile</option>
                            <option value="Risorse non disponibili">Risorse non disponibili</option>
                            <option value="Servizio fuori servizio">Servizio non disponibile</option>
                        </select>

                        <button type="button" class="btn-conferma-rifiuto" data-id="<%= id %>">Conferma rifiuto</button>
                        <button type="button" class="btn-annulla-rifiuto" data-id="<%= id %>">Annulla</button>
                    </div>
                <% } else { %>
                    -
                <% } %>
            </td>
        </tr>
    <%
            }
        }
    %>
    </tbody>
</table>

<a href="<%= ctx %>/logout" class="logout-btn">
    <i class="fas fa-sign-out-alt"></i> Logout
</a>

<script>
  window.USTRIKE_CTX = "<%= ctx %>";
  window.USTRIKE_FILTER = "<%= filter %>";
</script>

<script src="${pageContext.request.contextPath}/static/JavaScript/dashboard.js"></script>
</body>
</html>
