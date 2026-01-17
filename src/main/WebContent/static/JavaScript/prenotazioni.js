document.addEventListener("DOMContentLoaded", function() {
    // --- FIX DI SICUREZZA PER CTX ---
    // Se la JSP non ha passato 'ctx', lo calcoliamo o usiamo una stringa vuota
    // per evitare che lo script vada in crash (ReferenceError).
    let contextPath = "";
    if (typeof ctx !== 'undefined') {
        contextPath = ctx;
    } else {
        console.warn("Attenzione: 'ctx' non è definito. Uso path relativo.");
    }
    
    console.log("--- UStrike Debug: JS Caricato ---");
    console.log("Context Path in uso:", contextPath);

    // 1. Riferimenti agli elementi HTML
    const dataInput = document.getElementById("data");
    const orarioSelect = document.getElementById("orario");
    const idServizioInput = document.getElementById("idServizio");
    const idRisorsaSelect = document.getElementById("idRisorsa");
    const numPartecipantiSelect = document.getElementById("numPartecipanti");
    const campiPartecipanti = document.getElementById("campiPartecipanti");
    const prenForm = document.getElementById("prenForm");

    // 2. Funzione principale: Chiama la Servlet
    function caricaRisorse() {
        if (!dataInput || !orarioSelect || !idServizioInput) return;

        const data = dataInput.value;
        const orario = orarioSelect.value;
        const idServizio = idServizioInput.value;

        console.log("Dati form -> Servizio:", idServizio, "Data:", data, "Orario:", orario);

        if (!data || !orario || !idServizio) {
            console.warn("Attendo compilazione di tutti i campi...");
            return;
        }

        // --- COSTRUZIONE URL ROBUSTA ---
        // Se contextPath c'è, lo usiamo. Altrimenti usiamo il percorso relativo.
        let url;
        if (contextPath) {
            // Caso standard: /NomeProgetto/cliente/risorse-disponibili
            url = contextPath + "/cliente/risorse-disponibili";
        } else {
            // Caso fallback: siamo già in /cliente/crea-prenotazione, quindi chiamiamo solo la servlet
            url = "risorse-disponibili";
        }
        
        // Aggiungiamo i parametri
        url += "?idServizio=" + idServizio + "&data=" + data + "&orario=" + orario;
        
        console.log("Eseguo FETCH verso URL:", url);

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error("Errore HTTP: " + response.status);
                return response.json();
            })
            .then(json => {
                console.log("Risposta JSON:", json);
                idRisorsaSelect.innerHTML = '<option value="">-- Seleziona --</option>';
                
                if (json.success && json.risorse && json.risorse.length > 0) {
                    json.risorse.forEach(r => {
                        let opt = document.createElement("option");
                        opt.value = r.id; 
                        opt.textContent = r.label;
                        idRisorsaSelect.appendChild(opt);
                    });
                    console.log("Caricate " + json.risorse.length + " risorse.");
                } else {
                    idRisorsaSelect.innerHTML = '<option value="">Nessuna risorsa libera</option>';
                }
            })
            .catch(err => {
                console.error("Errore AJAX:", err);
                idRisorsaSelect.innerHTML = '<option value="">Errore di sistema</option>';
            });
    }

    // 3. Trigger eventi
    if (dataInput) dataInput.addEventListener("change", caricaRisorse);
    if (orarioSelect) orarioSelect.addEventListener("change", caricaRisorse);
    if (idServizioInput && idServizioInput.tagName === 'SELECT') {
        idServizioInput.addEventListener("change", caricaRisorse);
    }

    // 4. Gestione dinamica partecipanti
    if (numPartecipantiSelect) {
        numPartecipantiSelect.addEventListener("change", function() {
            const n = parseInt(this.value);
            campiPartecipanti.innerHTML = ""; 
            for (let i = 1; i <= n; i++) {
                const div = document.createElement("div");
                div.className = "input-group";
                div.innerHTML = `
                    <label style="display:block; margin-top:5px;">Nome Partecipante ${i}</label>
                    <input type="text" name="partecipante${i}" required style="width:100%; padding:8px;">
                `;
                campiPartecipanti.appendChild(div);
            }
        });
    }

    // 5. Invio Prenotazione
	// Cerca questa parte nel tuo prenotazioni.js
	prenForm.addEventListener("submit", function(e) {
	    e.preventDefault(); // Impedisce il ricaricamento classico della pagina
	    
	    const formData = new URLSearchParams(new FormData(prenForm));
	    
	    fetch(ctx + "/cliente/crea-prenotazione", {
	        method: 'POST',
	        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	        body: formData
	    })
	    .then(res => res.json())
	    .then(json => {
	        const msgDiv = document.getElementById("msg");
	        
	        if (json.success) {
	            // 1. Mostra un messaggio di successo (opzionale, dato che cambiamo pagina)
	            msgDiv.innerHTML = `<p style="color:green">${json.message}</p>`;
	            
	            // 2. REINDIRIZZAMENTO
	            // Usiamo ctx per assicurarci che l'URL sia corretto (es: /UStrike/cliente/prenotazioni)
	            setTimeout(function() {
	                window.location.href = ctx + "/cliente/prenotazioni";
	            }, 1000); // Aspetta 1 secondo per far leggere il messaggio di successo, poi cambia pagina
	            
	        } else {
	            // Gestione errore (rimane nella pagina attuale)
	            msgDiv.innerHTML = `<p style="color:red">${json.error}</p>`;
	        }
	    })
	    .catch(err => {
	        console.error("Errore:", err);
	        document.getElementById("msg").innerHTML = `<p style="color:red">Errore tecnico nell'invio.</p>`;
	    });
	});
});