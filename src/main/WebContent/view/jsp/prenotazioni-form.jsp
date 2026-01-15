<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.ustrike.model.dto.Servizio" %>

<%
    Integer idServizioSelezionato = (Integer) request.getAttribute("idServizioSelezionato");
    String tipo = (String) request.getAttribute("tipo"); // bowling | kart
    String ctx = request.getContextPath();
    List<Servizio> servizi = (List<Servizio>) request.getAttribute("servizi");
    if (servizi == null) servizi = Collections.emptyList();

    // Logica partecipanti: 6 per bowling, 15 per kart (o 15 come da tuo DB per kart)
    int maxPartecipanti = 1;
    if ("bowling".equalsIgnoreCase(tipo)) {
        maxPartecipanti = 6;
    } else if ("kart".equalsIgnoreCase(tipo)) {
        maxPartecipanti = 15;
    } else {
        maxPartecipanti = 6; // Default
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UStrike - Nuova Prenotazione</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/prenotazioni.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css">
</head>
<body>

<div class="logo">
    <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
</div>

<div class="container">
    <h2>Prenota la tua sfida: <%= (tipo != null ? tipo.toUpperCase() : "Servizio") %></h2>
    
    <div id="msg" style="margin-bottom: 20px; font-weight: bold; text-align: center;"></div>

    <form id="prenForm">
        <% if (idServizioSelezionato != null) { %>
            <input type="hidden" name="idServizio" id="idServizio" value="<%= idServizioSelezionato %>">
        <% } else { %>
            <div class="form-group">
                <label for="idServizio">Seleziona Servizio</label>
                <select name="idServizio" id="idServizio" required>
                    <option value="">-- Seleziona --</option>
                    <% for (Servizio s : servizi) { %>
                        <option value="<%= s.getIDServizio() %>"><%= s.getNomeServizio() %></option>
                    <% } %>
                </select>
            </div>
        <% } %>

        <div class="form-group">
            <label for="data">Data</label>
            <input type="date" name="data" id="data" required min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()) %>">
        </div>

        <div class="form-group">
            <label for="orario">Fascia oraria</label>
            <select name="orario" id="orario" required>
                <option value="">-- Seleziona prima la data --</option>
                <optgroup label="Sera">
                    <% for (int h = 17; h <= 23; h++) {
                        String hh = (h < 10 ? "0" + h : "" + h); %>
                        <option value="<%= hh %>:00"><%= hh %>:00 - <%= hh %>:59</option>
                    <% } %>
                </optgroup>
                <optgroup label="Notte (Oltre mezzanotte)">
                    <% for (int h = 0; h <= 2; h++) {
                        String hh = "0" + h; %>
                        <option value="<%= hh %>:00"><%= hh %>:00 - <%= hh %>:59</option>
                    <% } %>
                </optgroup>
            </select>
        </div>

        <div class="form-group">
            <label for="idRisorsa">Risorsa Disponibile (Piste/Kart)</label>
            <select name="idRisorsa" id="idRisorsa" required>
                <option value="">-- Scegli data e orario --</option>
            </select>
        </div>

        <div class="form-group">
            <label for="numPartecipanti">Numero partecipanti</label>
            <select id="numPartecipanti" name="numPartecipanti" required>
                <option value="">-- Quanti siete? --</option>
                <% for (int i = 1; i <= maxPartecipanti; i++) { %>
                    <option value="<%= i %>"><%= i %></option>
                <% } %>
            </select>
        </div>

        <div id="campiPartecipanti" class="dynamic-fields"></div>

        <button type="submit" class="submit-btn">Invia Richiesta di Prenotazione</button>
    </form>

    <a href="${pageContext.request.contextPath}/cliente/home" class="home-btn">
    <i class="fas fa-home"></i> Home
	</a>
</div>

<script>
    // Passiamo il context path al file JS esterno
    var ctx = '<%= ctx %>';
</script>
<script src="${pageContext.request.contextPath}/static/js/prenotazioni.js"></script>

</body>
</html>