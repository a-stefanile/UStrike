<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.ustrike.model.dto.Servizio" %>

<%
    Integer idServizioSelezionato =
        (Integer) request.getAttribute("idServizioSelezionato");
%>

<%
    String ctx = request.getContextPath();
    List<Servizio> servizi = (List<Servizio>) request.getAttribute("servizi");
    if (servizi == null) servizi = Collections.emptyList();
%>

<!DOCTYPE html>
<html>
	<head>
	    <meta charset="UTF-8">
	    <title>Nuova prenotazione</title>
	    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/prenotazioni.css">
	    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css">
	</head>
	<body>
	<!-- Logo -->
	<div class="logo">
	    <img src="${pageContext.request.contextPath}/static/images/logo.png" alt="UStrike Logo">
	</div>
	
	<h2>Nuova prenotazione</h2>
	
	<div id="msg"></div>
	
	<form id="prenForm">
	    <label>Data</label>
	    <input type="date" name="data" id="data" required>
	
	   <label>Fascia oraria</label>
		<select name="orario" id="orario" required>
		    <option value="">-- Seleziona --</option>
		
		    <% 
		        // Ore serali: dalle 17 alle 23 dello stesso giorno
		        for (int h = 17; h <= 23; h++) {
		            String hh = (h < 10 ? "0" + h : "" + h);
		    %>
		        <option value="<%= hh %>:00"><%= hh %>:00 - <%= hh %>:59</option>
		    <% } %>
		
		    <% 
		        // Ore notturne: 00 alle 02 del giorno successivo
		        for (int h = 0; h <= 2; h++) {
		            String hh = "0" + h;
		    %>
		        <option value="<%= hh %>:00"><%= hh %>:00 - <%= hh %>:59</option>
		    <% } %>
		</select>


		<% if (idServizioSelezionato == null) { %>
		    <label>Servizio</label>
		    <select name="idServizio" id="idServizio" required>
		        <option value="">-- Seleziona --</option>
		        <% for (Servizio s : servizi) { %>
		            <option value="<%= s.getIDServizio() %>"><%= s.getNomeServizio() %></option>
		        <% } %>
		    </select>
		<% } else { %>
		    <input type="hidden" name="idServizio" id="idServizio"
		           value="<%= idServizioSelezionato %>">
		<% } %>
	
	
	    <label>Risorsa disponibile</label>
	    <select name="idRisorsa" id="idRisorsa" required>
	        <option value="">-- Seleziona data/orario per visualizzare --</option>
	    </select>
	
	    <label>Partecipanti</label>
	    <textarea name="partecipanti" id="partecipanti" rows="3"
	              placeholder="Es: Mario Rossi, Luigi Verdi" required></textarea>
	
	    <button type="submit">Invia richiesta</button>
	</form>
	
	<script>
	function showMsg(text, ok) {
	    const msg = document.getElementById("msg");
	    msg.style.display = "block";
	    msg.style.borderColor = ok ? "#2e7d32" : "#c62828";
	    msg.textContent = text;
	}
	
	function resetRisorse(text) {
	    const sel = document.getElementById("idRisorsa");
	    sel.innerHTML = "";
	    const opt = document.createElement("option");
	    opt.value = "";
	    opt.textContent = text;
	    sel.appendChild(opt);
	}
	
	async function loadRisorse() {
	    const idServizio = document.getElementById("idServizio").value;
	    const data = document.getElementById("data").value;
	    const orario = document.getElementById("orario").value;
	
	    if (!idServizio || !data || !orario) {
	        resetRisorse("-- Seleziona servizio/data/orario --");
	        return;
	    }
	
	    resetRisorse("Caricamento...");
	
	    try {
	        const qs = new URLSearchParams({ idServizio, data, orario });
	        const res = await fetch("<%= ctx %>/cliente/risorse-disponibili?" + qs.toString(), {
	            method: "GET",
	            headers: { "Accept": "application/json" }
	        });
	
	        const json = await res.json();
	
	        if (!res.ok || !json.success) {
	            resetRisorse((json && json.error) ? json.error : "Nessuna risorsa");
	            return;
	        }
	
	        const sel = document.getElementById("idRisorsa");
	        sel.innerHTML = "";
	
	        if (!json.risorse || json.risorse.length === 0) {
	            resetRisorse("Nessuna risorsa disponibile");
	            return;
	        }
	
	        const ph = document.createElement("option");
	        ph.value = "";
	        ph.textContent = "-- Seleziona --";
	        sel.appendChild(ph);
	
	        for (const r of json.risorse) {
	            const opt = document.createElement("option");
	            opt.value = r.id;
	            opt.textContent = r.label;
	            sel.appendChild(opt);
	        }
	    } catch (e) {
	        resetRisorse("Errore caricamento risorse");
	    }
	}
	
	document.getElementById("idServizio").addEventListener("change", loadRisorse);
	document.getElementById("data").addEventListener("change", loadRisorse);
	document.getElementById("orario").addEventListener("change", loadRisorse);
	
	document.getElementById("prenForm").addEventListener("submit", async (e) => {
	    e.preventDefault();
	
	    showMsg("Invio richiesta...", true);
	
	    const body = new URLSearchParams(new FormData(e.target));
	
	    try {
	        const res = await fetch("<%= ctx %>/cliente/crea-prenotazione", {
	            method: "POST",
	            headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
	            body: body.toString()
	        });
	
	        const json = await res.json();
	
	        if (json.success) {
	            showMsg(json.message || "Richiesta inviata con successo. Attendi la conferma del nostro staff.", true);
	
	            // redirect dopo 2.5s
	            setTimeout(() => {
	                window.location.href = "<%= ctx %>/cliente/prenotazioni";
	            }, 2500);
	        } else {
	            showMsg(json.error || "Creazione fallita.", false);
	        }
	    } catch (err) {
	        showMsg("Errore di comunicazione col server.", false);
	    }
	});
	</script>
	
	<!-- Bottone per tornare alla Home Cliente -->
	<a href="${pageContext.request.contextPath}/cliente/home" class="home-btn">
	    <i class="fas fa-home"></i> Home
	</a>
	
	</body>
</html>
