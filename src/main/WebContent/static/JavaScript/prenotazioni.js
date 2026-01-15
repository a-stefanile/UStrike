document.addEventListener("DOMContentLoaded", function() {
    const prenForm = document.getElementById("prenForm");
    const dataInput = document.getElementById("data");
    const orarioSelect = document.getElementById("orario");
    const idServizioInput = document.getElementById("idServizio");
    const idRisorsaSelect = document.getElementById("idRisorsa");
    const numPartecipantiSelect = document.getElementById("numPartecipanti");
    const campiPartecipanti = document.getElementById("campiPartecipanti");

    // 1. Gestione dinamica campi partecipanti
    numPartecipantiSelect.addEventListener("change", function() {
        const n = parseInt(this.value);
        campiPartecipanti.innerHTML = ""; 
        for (let i = 1; i <= n; i++) {
            const label = document.createElement("label");
            label.textContent = "Nome Partecipante " + i;
            const input = document.createElement("input");
            input.type = "text";
            input.name = "partecipante" + i;
            input.required = true;
            campiPartecipanti.appendChild(label);
            campiPartecipanti.appendChild(input);
        }
    });

    // 2. Caricamento Risorse
    function caricaRisorse() {
        const data = dataInput.value;
        const orario = orarioSelect.value;
        const idServizio = idServizioInput.value;

        if (!data || !orario || !idServizio) return;

        fetch(`${ctx}/cliente/risorse-disponibili?idServizio=${idServizio}&data=${data}&orario=${orario}`)
            .then(res => res.json())
            .then(json => {
                idRisorsaSelect.innerHTML = '<option value="">-- Seleziona --</option>';
                if (json.success && json.risorse.length > 0) {
                    json.risorse.forEach(r => {
                        let opt = document.createElement("option");
                        opt.value = r.id;
                        opt.textContent = r.label;
                        idRisorsaSelect.appendChild(opt);
                    });
                } else {
                    idRisorsaSelect.innerHTML = '<option value="">Nessuna risorsa disponibile</option>';
                }
            });
    }

    dataInput.addEventListener("change", caricaRisorse);
    orarioSelect.addEventListener("change", caricaRisorse);

    // 3. Invio Form via AJAX
    prenForm.addEventListener("submit", function(e) {
        e.preventDefault();
        const formData = new URLSearchParams(new FormData(prenForm));

        fetch(`${ctx}/cliente/crea-prenotazione`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
        .then(res => res.json())
        .then(json => {
            const msgDiv = document.getElementById("msg");
            if (json.success) {
                msgDiv.innerHTML = `<p style="color:green">${json.message}</p>`;
                prenForm.reset();
                campiPartecipanti.innerHTML = "";
            } else {
                msgDiv.innerHTML = `<p style="color:red">${json.error}</p>`;
            }
        });
    });
});