package org.xbery.artbeams.articles.admin

import net.formio.*
import net.formio.upload.UploadedFile
import org.xbery.artbeams.articles.domain.EditedArticle
import org.xbery.artbeams.common.form.FormUtils
import java.util.*

open class ArticleForm {
    companion object {
        val definition: FormMapping<EditedArticle> = Forms.basic(EditedArticle::class.java, "article")
            .field<String>("id", Field.HIDDEN)
            .field<String?>("externalId", Field.TEXT)
            .field<String>("slug", Field.TEXT)
            .field<String>("title", Field.TEXT)
            .field<String>("image", Field.TEXT)
            .field<UploadedFile>("file", Field.FILE_UPLOAD)
            .field<String>("perex", Field.TEXT_AREA)
            .field<String>("bodyMarkdown", Field.TEXT_AREA)
            .field(Forms.field<Date>("validFrom", Field.DATE_TIME).pattern(FormUtils.DateTimePattern).build())
            .field(Forms.field<Date?>("validTo", Field.DATE_TIME).pattern(FormUtils.DateTimePattern).build())
            .field<String>(
                "keywords",
                Field.TEXT
            )
            .field<Boolean>("showOnBlog", Field.CHECK_BOX)
            .field<List<String>>("categories", Field.DROP_DOWN_CHOICE).build(FormUtils.CzConfig)
    }
}
