document.addEventListener("DOMContentLoaded", () => {
  const stepTemplate = document.getElementById("workflow-step-template");
  if (!(stepTemplate instanceof HTMLTemplateElement)) return;

  document.querySelectorAll("[data-workflow-form]").forEach((form) => {
    const stepList = form.querySelector("[data-workflow-step-list]");
    form.querySelector("[data-add-workflow-step]")?.addEventListener("click", () => {
      stepList?.append(stepTemplate.content.cloneNode(true));
    });
    form.addEventListener("click", (event) => {
      const target = event.target;
      if (!(target instanceof HTMLElement) || !target.matches("[data-remove-workflow-step]")) return;
      const rows = stepList?.querySelectorAll("[data-workflow-step]") ?? [];
      if (rows.length > 1) target.closest("[data-workflow-step]")?.remove();
    });
  });
});
