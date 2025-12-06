<#import "/member/memberLayout.ftl" as layout>
<@layout.page>

<div class="container">
  <div class="row">
    <div class="col">
      <h2>Moje objednávky</h2>

      <#if orders?has_content>
        <div class="order-list">
          <#list orders as order>
            <div class="card mb-3">
              <div class="card-header d-flex justify-content-between align-items-center">
                <div>
                  <h5 class="mb-0">Objednávka č. ${order.orderNumber}</h5>
                  <small class="text-muted">${order.orderTime?string('d.M.yyyy, HH:mm')}</small>
                </div>
                <div>
                  <#if order.state.name() == "PAID">
                    <span class="badge bg-success">Zaplaceno</span>
                  <#elseif order.state.name() == "SHIPPED">
                    <span class="badge bg-info">Odesláno</span>
                  <#elseif order.state.name() == "CONFIRMED">
                    <span class="badge bg-warning">Potvrzeno</span>
                  <#elseif order.state.name() == "CREATED">
                    <span class="badge bg-secondary">Vytvořeno</span>
                  <#else>
                    <span class="badge bg-secondary">${order.state.name()}</span>
                  </#if>
                </div>
              </div>
              <div class="card-body">
                <h6>Produkty:</h6>
                <table class="table table-sm">
                  <thead>
                    <tr>
                      <th>Produkt</th>
                      <th>Množství</th>
                      <th>Cena</th>
                      <th>Staženo</th>
                    </tr>
                  </thead>
                  <tbody>
                    <#list order.items as item>
                      <tr>
                        <td>${item.productName}</td>
                        <td>${item.quantity}</td>
                        <td>${item.price.amount} ${item.price.currency}</td>
                        <td>
                          <#if item.downloaded??>
                            <span class="badge bg-success">
                              <i class="fas fa-check"></i> ${item.downloaded?string('d.M.yyyy')}
                            </span>
                          <#else>
                            <#if order.state.name() == "PAID" || order.state.name() == "SHIPPED">
                              <a href="/clenska-sekce/${item.productId}" class="btn btn-sm btn-primary">
                                <i class="fas fa-download"></i> Stáhnout
                              </a>
                            <#else>
                              <span class="text-muted">Čeká na platbu</span>
                            </#if>
                          </#if>
                        </td>
                      </tr>
                    </#list>
                  </tbody>
                </table>

                <div class="row mt-3">
                  <div class="col-md-6">
                    <strong>Celková cena:</strong> ${order.price.amount} ${order.price.currency}
                  </div>
                  <#if order.paidTime??>
                    <div class="col-md-6 text-end">
                      <small class="text-muted">Zaplaceno: ${order.paidTime?string('d.M.yyyy, HH:mm')}</small>
                    </div>
                  </#if>
                </div>

                <#if order.paymentMethod??>
                  <div class="mt-2">
                    <small><strong>Způsob platby:</strong> ${order.paymentMethod}</small>
                  </div>
                </#if>
              </div>
            </div>
          </#list>
        </div>
      <#else>
        <div class="alert alert-info mt-4" role="alert">
          <i class="fas fa-info-circle"></i> Zatím jste nevytvořili žádnou objednávku.
        </div>
        <div class="mt-3">
          <a href="/" class="btn btn-primary">Prohlédnout produkty</a>
        </div>
      </#if>
    </div>
  </div>
</div>

</@layout.page>
