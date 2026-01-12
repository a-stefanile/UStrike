<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.ustrike.model.dto.PrenotazioneView" %>

<%
  String ctx = request.getContextPath();
  List<PrenotazioneView> prenotazioni = (List<PrenotazioneView>) request.getAttribute("prenotazioni");
  if (prenotazioni == null) prenotazioni = Collections.emptyList();

  SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Le mie prenotazioni</title>
  <style>
    body { font-family: Arial, sans-serif; }
    table { border-collapse: collapse; width: 100%; max-width: 1100px; }
    th, td { border: 1px solid #ddd; padding: 8px; }
    th { background: #f3f3f3; text-align: left; }
    .badge { padding: 2px 8px; border: 1px solid #ccc; border-radius: 10px; display:inline-block; }
    .ok { border-color:#2e7d32; }
    .warn { border-color:#ef6c00; }
    .err { border-color:#c62828; }
  </style>
</head>
<body>

<h2>Le mie prenotazioni</h2>

<div style="margin-bottom:10px;">
  <a href="<%= ctx %>/cliente/crea-prenotazione">Nuova prenotazione</a> |
  <a href="<%= ctx %>/cliente/dashboard">Dashboard</a> |
  <a href="<%= ctx %>/logout">Logout</a>
</div>

<% if (prenotazioni.isEmpty()) { %>
  <p>Nessuna prenotazione presente.</p>
<% } else { %>

<table>
  <thead>
    <tr>
      <th>ID</th>
      <th>Servizio</th>
      <th>Data</th>
      <th>Ora</th>
      <th>Risorsa</th>
      <th>Capacità</th>
      <th>Stato</th>
      <th>Partecipanti</th>
    </tr>
  </thead>

  <tbody>
    <% for (PrenotazioneView p : prenotazioni) {
         String stato = (p.getStatoPrenotazione() == null) ? "" : p.getStatoPrenotazione();
         String cls = "badge";
         if ("Confermata".equalsIgnoreCase(stato)) cls += " ok";
         else if ("In attesa".equalsIgnoreCase(stato)) cls += " warn";
         else cls += " err";

         String dataStr = (p.getData() == null) ? "-" : dfDate.format(p.getData());
         String oraStr  = (p.getOrario() == null) ? "-" : dfTime.format(p.getOrario());
    %>
      <tr>
        <td><%= p.getIDPrenotazione() %></td>
        <td><%= p.getNomeServizio() %></td>
        <td><%= dataStr %></td>
        <td><%= oraStr %></td>
        <td><%= p.getIDRisorsa() %></td>
        <td><%= p.getCapacitaRisorsa() %></td>
        <td><span class="<%= cls %>"><%= stato %></span></td>
        <td><%= p.getPartecipanti() %></td>
      </tr>
    <% } %>
  </tbody>
</table>

<% } %>

</body>
</html>
