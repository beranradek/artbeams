<#import "/adminLayout.ftl" as layout>
<@layout.page noUp=true>

<#if !authorized>
  <div class="alert alert-info" role="alert">
    <h4 class="alert-heading">Authorization Required</h4>
    <p>To access Google Search Console metrics, you need to authorize the application.</p>
    <hr>
    <a href="${authUrl}" class="btn btn-primary">Authorize with Google</a>
  </div>
<#else>

  <#if error??>
    <div class="alert alert-danger" role="alert">
      ${error}
    </div>
  </#if>

  <div class="row mb-3">
    <div class="col-md-12">
      <h2>Google Search Console Metrics</h2>
    </div>
  </div>

  <!-- Date Range Filter -->
  <form method="GET" action="/admin/search-console" class="mb-4">
    <div class="row">
      <div class="col-md-4">
        <label for="startDate">Start Date</label>
        <input type="date" class="form-control" id="startDate" name="startDate" value="${startDate!}" required />
      </div>
      <div class="col-md-4">
        <label for="endDate">End Date</label>
        <input type="date" class="form-control" id="endDate" name="endDate" value="${endDate!}" required />
      </div>
      <div class="col-md-4">
        <label>&nbsp;</label>
        <button type="submit" class="btn btn-primary form-control">Update</button>
      </div>
    </div>
  </form>

  <!-- Overview Metrics -->
  <#if metrics??>
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Impressions</h5>
            <p class="card-text display-4">${metrics.impressions?c}</p>
            <p class="text-muted">Total impressions in search results</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Clicks</h5>
            <p class="card-text display-4">${metrics.clicks?c}</p>
            <p class="text-muted">Total clicks from search results</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">CTR</h5>
            <p class="card-text display-4">${(metrics.ctr * 100)?string["0.00"]}%</p>
            <p class="text-muted">Click-through rate</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Avg. Position</h5>
            <p class="card-text display-4">${metrics.position?string["0.0"]}</p>
            <p class="text-muted">Average position in search results</p>
          </div>
        </div>
      </div>
    </div>
  </#if>

  <!-- Top Pages -->
  <#if topPages?? && topPages?size gt 0>
    <div class="row mb-4">
      <div class="col-md-12">
        <h3>Top Performing Pages</h3>
        <table class="table table-sm">
          <thead>
            <tr>
              <th>Page</th>
              <th class="text-right">Impressions</th>
              <th class="text-right">Clicks</th>
              <th class="text-right">CTR</th>
              <th class="text-right">Avg. Position</th>
            </tr>
          </thead>
          <tbody>
            <#list topPages as page>
              <tr>
                <td><a href="${page.page}" target="_blank" class="text-truncate d-inline-block" style="max-width: 400px;">${page.page}</a></td>
                <td class="text-right">${page.impressions?c}</td>
                <td class="text-right">${page.clicks?c}</td>
                <td class="text-right">${(page.ctr * 100)?string["0.00"]}%</td>
                <td class="text-right">${page.position?string["0.0"]}</td>
              </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </div>
  </#if>

  <!-- Top Queries -->
  <#if topQueries?? && topQueries?size gt 0>
    <div class="row mb-4">
      <div class="col-md-12">
        <h3>Top Performing Queries</h3>
        <table class="table table-sm">
          <thead>
            <tr>
              <th>Query</th>
              <th class="text-right">Impressions</th>
              <th class="text-right">Clicks</th>
              <th class="text-right">CTR</th>
              <th class="text-right">Avg. Position</th>
            </tr>
          </thead>
          <tbody>
            <#list topQueries as query>
              <tr>
                <td>${query.query}</td>
                <td class="text-right">${query.impressions?c}</td>
                <td class="text-right">${query.clicks?c}</td>
                <td class="text-right">${(query.ctr * 100)?string["0.00"]}%</td>
                <td class="text-right">${query.position?string["0.0"]}</td>
              </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </div>
  </#if>

  <!-- Sitemaps -->
  <#if sitemaps?? && sitemaps?size gt 0>
    <div class="row mb-4">
      <div class="col-md-12">
        <h3>Sitemap Status</h3>
        <#if hasIndexingIssues>
          <div class="alert alert-warning" role="alert">
            <strong>Warning:</strong> Some sitemaps have indexing issues (errors or warnings).
          </div>
        </#if>
        <table class="table table-sm">
          <thead>
            <tr>
              <th>Path</th>
              <th>Type</th>
              <th>Last Submitted</th>
              <th>Last Downloaded</th>
              <th class="text-right">Warnings</th>
              <th class="text-right">Errors</th>
            </tr>
          </thead>
          <tbody>
            <#list sitemaps as sitemap>
              <tr>
                <td>${sitemap.path}</td>
                <td>
                  <#if sitemap.isSitemapsIndex>Index<#else>Sitemap</#if>
                  <#if sitemap.isPending> <span class="badge badge-info">Pending</span></#if>
                </td>
                <td>${sitemap.lastSubmitted!"-"}</td>
                <td>${sitemap.lastDownloaded!"-"}</td>
                <td class="text-right"><#if sitemap.warnings gt 0><span class="text-warning">${sitemap.warnings?c}</span><#else>0</#if></td>
                <td class="text-right"><#if sitemap.errors gt 0><span class="text-danger">${sitemap.errors?c}</span><#else>0</#if></td>
              </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </div>
  </#if>

  <style>
    .card {
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .card-body {
      padding: 1.5rem;
    }
    .display-4 {
      font-size: 2.5rem;
      font-weight: 300;
      color: #007bff;
    }
    .text-muted {
      font-size: 0.875rem;
    }
    .table th {
      border-top: none;
    }
    .text-truncate {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  </style>

</#if>

</@layout.page>
