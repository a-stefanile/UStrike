function showMsg(text, ok) {
  var el = document.getElementById("msg");
  el.textContent = text;
  el.className = "msg " + (ok ? "ok" : "err");
  el.style.display = "block";
}

function hideAllRifiutoBox() {
  var list = document.querySelectorAll(".rifiuto-box");
  for (var i = 0; i < list.length; i++) {
    list[i].style.display = "none";
  }
}

function openRifiuto(idPrenotazione) {
  hideAllRifiutoBox();
  var box = document.getElementById("rifiuto-box-" + idPrenotazione);
  if (box) box.style.display = "inline-block";
}

function closeRifiuto(idPrenotazione) {
  var box = document.getElementById("rifiuto-box-" + idPrenotazione);
  if (box) box.style.display = "none";
}

function updateStato(idPrenotazione, action, motivo) {
  var ctx = window.USTRIKE_CTX || "";
  var filter = window.USTRIKE_FILTER || "inAttesa";

  var body = new URLSearchParams();
  body.append("action", action);
  body.append("idPrenotazione", String(idPrenotazione));
  if (action === "rifiuta") {
    body.append("motivo", motivo || "");
  }

  fetch(ctx + "/staff/catalogo", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: body.toString()
  })
    .then(function (res) { return res.json(); })
    .then(function (data) {
      if (data.success) {
        showMsg("Operazione completata.", true);
        setTimeout(function () {
          window.location.href = ctx + "/staff/catalogo?filter=" + encodeURIComponent(filter);
        }, 300);
      } else {
        showMsg(data.error || "Operazione fallita.", false);
      }
    })
    .catch(function () {
      showMsg("Errore di comunicazione col server.", false);
    });
}

document.addEventListener("click", function (e) {
  var btn = e.target.closest ? e.target.closest("button") : e.target;
  if (!btn) return;

  if (btn.classList.contains("btn-accetta")) {
    var idAcc = btn.getAttribute("data-id");
    updateStato(idAcc, "accetta");
    return;
  }

  if (btn.classList.contains("btn-rifiuta")) {
    var idRif = btn.getAttribute("data-id");
    openRifiuto(idRif);
    return;
  }

  if (btn.classList.contains("btn-conferma-rifiuto")) {
    var idConf = btn.getAttribute("data-id");
    var sel = document.getElementById("motivo-" + idConf);
    var motivo = sel ? sel.value : "";

    if (!motivo) {
      showMsg("Seleziona un motivo di rifiuto.", false);
      return;
    }

    updateStato(idConf, "rifiuta", motivo);
    return;
  }

  if (btn.classList.contains("btn-annulla-rifiuto")) {
    var idAnn = btn.getAttribute("data-id");
    closeRifiuto(idAnn);
  }
});
