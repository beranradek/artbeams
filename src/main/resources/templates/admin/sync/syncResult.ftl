<#import "/adminLayout.ftl" as layout>
<@layout.page>
    <div class="container mt-4">
        <h1>Sync Result</h1>

        <#if result.success>
            <div class="alert alert-success" role="alert">
                <h4 class="alert-heading">
                    <i class="fas fa-check-circle"></i> Sync Completed Successfully
                </h4>
                <hr>
                <h5>Summary:</h5>
                <ul>
                    <li><strong>Articles Created:</strong> ${result.articlesCreated}</li>
                    <li><strong>Articles Updated:</strong> ${result.articlesUpdated}</li>
                    <li><strong>Localizations Created:</strong> ${result.localisationsCreated}</li>
                    <li><strong>Localizations Updated:</strong> ${result.localisationsUpdated}</li>
                </ul>
                <p class="mb-0">
                    <strong>Note:</strong> All external IDs have been removed from synced articles to prevent accidental updates to paired Google Documents.
                </p>
            </div>
        <#else>
            <div class="alert alert-danger" role="alert">
                <h4 class="alert-heading">
                    <i class="fas fa-exclamation-circle"></i> Sync Failed
                </h4>
                <hr>
                <h5>Partial Results:</h5>
                <ul>
                    <li><strong>Articles Created:</strong> ${result.articlesCreated}</li>
                    <li><strong>Articles Updated:</strong> ${result.articlesUpdated}</li>
                    <li><strong>Localizations Created:</strong> ${result.localisationsCreated}</li>
                    <li><strong>Localizations Updated:</strong> ${result.localisationsUpdated}</li>
                </ul>
                <#if result.errorMessage??>
                    <p class="mb-0">
                        <strong>Error:</strong> ${result.errorMessage}
                    </p>
                </#if>
            </div>
        </#if>

        <div class="mt-3">
            <a href="/admin" class="btn btn-primary">
                <i class="fas fa-home"></i> Back to Admin
            </a>
            <a href="/admin/articles" class="btn btn-secondary">
                <i class="fas fa-newspaper"></i> View Articles
            </a>
            <a href="/admin/localisations" class="btn btn-secondary">
                <i class="fas fa-language"></i> View Localizations
            </a>
        </div>
    </div>
</@layout.page>
