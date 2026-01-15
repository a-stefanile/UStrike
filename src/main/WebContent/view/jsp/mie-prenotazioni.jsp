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
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Le mie prenotazioni</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="<%= ctx %>/static/css/mie-prenotazioni.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css">
</head>
<body>

<!-- Logo in alto a destra -->
<div class="logo">
    <img src="<%= ctx %>/static/images/logo.png" alt="UStrike Logo">
</div>

<h2>Le mie prenotazioni</h2>

<!-- Pulsante sempre visibile -->
<a href="<%= ctx %>/cliente/crea-prenotazione" class="btn-prenota new-pren-btn">Nuova prenotazione</a>

<% if (prenotazioni.isEmpty()) { %>
  <p class="no-pren">Nessuna prenotazione presente.</p>
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
         String stato = (p.getStatoPrenotazione() == null) ? "-" : p.getStatoPrenotazione();
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

<!-- Bottone per tornare alla Home Cliente -->
<a href="${pageContext.request.contextPath}/cliente/home" class="home-btn">
    <i class="fas fa-home"></i> Home
</a>

<!-- Logout con icona -->
<a href="${pageContext.request.contextPath}/logout" class="logout-btn">
    <i class="fas fa-sign-out-alt"></i> Logout
</a>

</body>
</html>