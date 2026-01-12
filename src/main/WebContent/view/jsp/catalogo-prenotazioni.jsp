<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ustrike.model.dto.Prenotazione" %>

<%
    String ctx = request.getContextPath();
    String filter = (String) request.getAttribute("filter");
    List<Prenotazione> prenotazioni = (List<Prenotazione>)request.getAttribute("prenotazioni");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>UStrike - Dashboard Staff</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px; }
        th { background: #f3f3f3; }
        .msg { margin: 10px 0; padding: 8px; border: 1px solid #ccc; display: none; }
        .msg.ok { border-color: #2e7d32; }
        .msg.err { border-color: #c62828; }
        button { margin-right: 6px; }
    </style>
</head>
<body>

<h2>Dashboard Staff - Catalogo Prenotazioni</h2>

<div>
    <a href="<%= ctx %>/staff/catalogo?filter=inAttesa">In attesa</a> |
    <a href="<%= ctx %>/staff/catalogo?filter=completate">Completate</a> |
    <a href="<%= ctx %>/staff/catalogo?filter=all">Tutte</a> |
    <a href="<%= ctx %>/logout">Logout</a>
</div>

<div id="msg" class="msg"></div>

<p>Filtro attivo: <b><%= filter %></b></p>

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
                String stato = p.getStatoPrenotazione();
                Integer idStaff = p.getIDStaff();
    %>
        <tr>
            <td><%= id %></td>
            <td><%= p.getData() %></td>
            <td><%= p.getOrario() %></td>
            <td><%= stato %></td>
            <td><%= p.getIDServizio() %></td>
            <td><%= p.getIDRisorsa() %></td>
            <td><%= p.getIDCliente() %></td>
            <td><%= (idStaff != null ? idStaff : "-") %></td>
            <td><%= p.getPartecipanti() %></td>
            <td>
                <% if ("inAttesa".equalsIgnoreCase(filter)) { %>
                    <button type="button" onclick="updateStato(<%= id %>, 'accetta')">Accetta</button>
                    <button type="button" onclick="updateStato(<%= id %>, 'rifiuta')">Rifiuta</button>
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

<script>
function showMsg(text, ok) {
    const el = document.getElementById("msg");
    el.textContent = text;
    el.className = "msg " + (ok ? "ok" : "err");
    el.style.display = "block";
}

async function updateStato(idPrenotazione, action) {
    const body = new URLSearchParams();
    body.append("action", action);
    body.append("idPrenotazione", String(idPrenotazione));

    try {
        const res = await fetch("<%= ctx %>/staff/catalogo", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: body.toString()
        });

        const data = await res.json();

        if (data.success) {
            showMsg("Operazione completata.", true);
            setTimeout(() => {
                window.location.href = "<%= ctx %>/staff/catalogo?filter=<%= filter %>";
            }, 300);
        } else {
            showMsg(data.error || "Operazione fallita.", false);
        }
    } catch (e) {
        showMsg("Errore di comunicazione col server.", false);
    }
}
</script>

</body>
</html>
