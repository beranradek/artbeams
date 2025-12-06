<#import "/adminLayout.ftl" as layout>
<@layout.page>
    <div class="container mt-4">
        <h1>Sync Error</h1>

        <div class="alert alert-danger" role="alert">
            <h4 class="alert-heading">
                <i class="fas fa-exclamation-triangle"></i> Configuration Error
            </h4>
            <p class="mb-0">${errorMessage}</p>
        </div>

        <div class="mt-3">
            <a href="/admin" class="btn btn-primary">
                <i class="fas fa-home"></i> Back to Admin
            </a>
            <a href="/admin/config" class="btn btn-secondary">
                <i class="fas fa-cog"></i> View Configuration
            </a>
        </div>
    </div>
</@layout.page>
