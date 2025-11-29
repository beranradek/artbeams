<#import "/adminLayout.ftl" as layout>
<@layout.page>
    <div class="container mt-4">
        <h1>Sync Content from Remote Database</h1>

        <div class="alert alert-warning" role="alert">
            <h4 class="alert-heading">Warning</h4>
            <p>This operation will sync articles and localizations from the remote database to your local database.</p>
            <hr>
            <ul>
                <li>New records will be <strong>created</strong> if they don't exist locally</li>
                <li>Existing records will be <strong>updated</strong> with remote data</li>
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
