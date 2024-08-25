package org.xbery.artbeams.common.ajax

/**
 * Body of AJAX response serialized and returned as a JSON.
 *
 * @author Radek Beran
 */
data class AjaxResponseBody(
    /** HTML content if some content should be re-placed in the page. */
    val htmlContent: String?,

    /**
     * Target redirect URI if redirection to another page
     * should be performed as a result of AJAX request
     * (e.g. in case of success submission).
     */
    val redirectUri: String? = null
)
