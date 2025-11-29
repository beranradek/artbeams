<#import "/adminLayout.ftl" as layout>
<@layout.page>
    <div class="container mt-4">
        <h1>Sync Content from Remote Database</h1>

        <div class="alert alert-warning" role="alert">
            <h4 class="alert-heading">Warning</h4>
            <p>This operation will sync categories, products, articles, localizations, and media from the remote database to your local database.</p>
            <hr>
            <ul>
                <li><strong>Categories:</strong> Matched by slug (not by ID). Existing categories with the same slug will be updated, new categories will be created with remote data</li>
                <li><strong>Products:</strong> Matched by slug (not by ID). Existing products with the same slug will be updated, new products will be created with remote data</li>
                <li><strong>Articles:</strong> Matched by slug (not by ID). Existing articles with the same slug will be updated, new articles will be created with remote data</li>
                <li><strong>Localizations:</strong> Matched by key (not by ID). Existing localizations with the same key will be updated, new localizations will be created</li>
                <li><strong>Media:</strong> Matched by filename, content type, and size (not by ID). Existing media with the same attributes will be updated, otherwise new media will be created</li>
                <li>All external IDs in synced articles will be <strong>removed</strong> to prevent accidental updates to paired Google Documents</li>
            </ul>
            <p class="mb-0">Are you sure you want to proceed?</p>
        </div>

        <form action="/admin/sync/execute" method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit" class="btn btn-primary">
                <i class="fas fa-sync-alt"></i> Yes, Sync Now
            </button>
            <a href="/admin" class="btn btn-secondary">
                <i class="fas fa-times"></i> Cancel
            </a>
        </form>
    </div>
</@layout.page>
