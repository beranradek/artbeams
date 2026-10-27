package org.xbery.artbeams.emailtemplates.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.name
import org.jooq.impl.DSL.table
import org.springframework.stereotype.Repository
import org.xbery.artbeams.common.assets.domain.AssetAttributes
import org.xbery.artbeams.emailtemplates.domain.EmailTemplate
import java.time.Instant

@Repository
class EmailTemplateRepository(private val dsl: DSLContext) {
    private val templates = table(name("email_templates"))
    private val id = field(name("id"), String::class.java)
    private val created = field(name("created"), Instant::class.java)
    private val createdBy = field(name("created_by"), String::class.java)
    private val modified = field(name("modified"), Instant::class.java)
    private val modifiedBy = field(name("modified_by"), String::class.java)
    private val key = field(name("template_key"), String::class.java)
    private val subject = field(name("subject"), String::class.java)
    private val bodyHtml = field(name("body_html"), String::class.java)
    private val system = field(name("system_template"), Boolean::class.java)

    fun findAll(): List<EmailTemplate> = dsl.selectFrom(templates).orderBy(key).fetch(::toEntity)
    fun findByKey(templateKey: String): EmailTemplate? = dsl.selectFrom(templates).where(key.eq(templateKey)).fetchOne(::toEntity)
    fun findById(templateId: String): EmailTemplate? = dsl.selectFrom(templates).where(id.eq(templateId)).fetchOne(::toEntity)
    fun save(entity: EmailTemplate): EmailTemplate {
        dsl.insertInto(templates).set(id, entity.id).set(created, entity.common.created).set(createdBy, entity.common.createdBy)
            .set(modified, entity.common.modified).set(modifiedBy, entity.common.modifiedBy).set(key, entity.templateKey)
            .set(subject, entity.subject).set(bodyHtml, entity.bodyHtml).set(system, entity.systemTemplate).execute()
        return requireNotNull(findById(entity.id))
    }
    fun update(entity: EmailTemplate): EmailTemplate {
        dsl.update(templates).set(modified, entity.common.modified).set(modifiedBy, entity.common.modifiedBy).set(key, entity.templateKey)
            .set(subject, entity.subject).set(bodyHtml, entity.bodyHtml).where(id.eq(entity.id)).execute()
        return requireNotNull(findById(entity.id))
    }
    fun delete(idValue: String): Boolean = dsl.deleteFrom(templates).where(id.eq(idValue)).execute() == 1
    private fun toEntity(record: org.jooq.Record): EmailTemplate = EmailTemplate(AssetAttributes(record[id]!!, record[created]!!, record[createdBy]!!, record[modified]!!, record[modifiedBy]!!), record[key]!!, record[subject]!!, record[bodyHtml]!!, record[system]!!)
}
