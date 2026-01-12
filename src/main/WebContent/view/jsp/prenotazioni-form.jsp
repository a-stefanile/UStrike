<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.ustrike.model.dto.Servizio" %>

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
    <style>
        body { font-family: Arial, sans-serif; }
        label { display:block; margin-top: 10px; }
        input, select, textarea { width: 380px; max-width: 100%; padding: 6px; }
        button { margin-top: 14px; padding: 8px 12px; }
        #msg { display:none; padding:10px; border:1px solid #ccc; margin:10px 0; }
    </style>
</head>
<body>

<h2>Nuova prenotazione</h2>

<div id="msg"></div>

<form id="prenForm">
    <label>Data</label>
    <input type="date" name="data" id="data" required>

    <label>Fascia oraria</label>
    <select name="orario" id="orario" required>
        <option value="">-- Seleziona --</option>
        <% for (int h = 0; h < 24; h++) {
            String hh = (h < 10 ? "0" + h : "" + h);
        %>
        <option value="<%= hh %>:00"><%= hh %>:00 - <%= hh %>:59</option>
        <% } %>
    </select>

    <label>Servizio</label>
    <select name="idServizio" id="idServizio" required>
        <option value="">-- Seleziona --</option>
        <% for (Servizio s : servizi) { %>
            <option value="<%= s.getIDServizio() %>"><%= s.getNomeServizio() %></option>
        <% } %>
    </select>

    <label>Risorsa disponibile</label>
    <select name="idRisorsa" id="idRisorsa" required>
        <option value="">-- Seleziona servizio/data/orario --</option>
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

</body>
</html>
