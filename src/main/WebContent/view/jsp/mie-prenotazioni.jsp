<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.ustrike.model.dto.PrenotazioneView" %>

<%
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
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/mie-prenotazioni.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css">
</head>
<body>

<div class="logo">
  <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
</div>

<h2>Le mie prenotazioni</h2>

<%
  String flash = (String) session.getAttribute("flashMsg");
  if (flash != null) {
    session.removeAttribute("flashMsg");
%>
  <div class="alert-success"><%= flash %></div>
<%
  }
%>

<a href="${pageContext.request.contextPath}/cliente/crea-prenotazione" class="btn-prenota new-pren-btn">
  Nuova prenotazione
</a>

<!-- MODALE CONFERMA -->
<dialog id="confirmDialog" class="modal">
  <h3>Conferma annullamento</h3>
  <p>Vuoi annullare questa prenotazione?</p>
  <div class="modal-actions">
    <button type="button" id="btnNo" class="btn btn-secondary">No</button>
    <button type="button" id="btnYes" class="btn btn-danger">Sì, annulla</button>
  </div>
</dialog>

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
      <th>Motivo rifiuto</th>
      <th>Azioni</th>
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

         String note = (p.getNoteStaff() == null) ? "" : p.getNoteStaff().trim();
         String motivoDaMostrare = "-";
         if ("Rifiutata".equalsIgnoreCase(stato) && !note.isEmpty()) motivoDaMostrare = note;
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
        <td><%= motivoDaMostrare %></td>

        <td>
          <% if ("In attesa".equalsIgnoreCase(stato)) { %>
            <form class="annullaForm"
                  action="${pageContext.request.contextPath}/cliente/annulla-prenotazione"
                  method="post">
              <input type="hidden" name="idPrenotazione" value="<%= p.getIDPrenotazione() %>">
              <button type="submit" class="btn-danger">Annulla</button>
            </form>
          <% } else { %>
            -
          <% } %>
        </td>
      </tr>
    <% } %>
  </tbody>
</table>

<% } %>

<a href="${pageContext.request.contextPath}/cliente/home" class="home-btn">
  <i class="fas fa-home"></i> Home
</a>

<a href="${pageContext.request.contextPath}/logout" class="logout-btn">
  <i class="fas fa-sign-out-alt"></i> Logout
</a>

<!-- JS esterno -->
<script src="${pageContext.request.contextPath}/static/JavaScript/annullaprenotazione.js" defer></script>

</body>
</html>
