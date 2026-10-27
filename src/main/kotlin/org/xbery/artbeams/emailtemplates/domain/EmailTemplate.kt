package org.xbery.artbeams.emailtemplates.domain

import org.xbery.artbeams.common.assets.domain.AssetAttributes

data class EmailTemplate(
    val common: AssetAttributes,
    val templateKey: String,
    val subject: String,
    val bodyHtml: String,
    val systemTemplate: Boolean
) {
    val id: String get() = common.id
}
