(() => {
  const dialog = document.getElementById("confirmDialog");
  const btnYes = document.getElementById("btnYes");
  const btnNo = document.getElementById("btnNo");

  if (!dialog || !btnYes || !btnNo) return;

  let pendingForm = null;

  document.querySelectorAll(".annullaForm").forEach(form => {
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      pendingForm = form;
      dialog.showModal();
    });
  });

  btnNo.addEventListener("click", () => {
    pendingForm = null;
    dialog.close();
  });

  btnYes.addEventListener("click", () => {
    dialog.close();
    if (pendingForm) pendingForm.submit();
  });
})();
