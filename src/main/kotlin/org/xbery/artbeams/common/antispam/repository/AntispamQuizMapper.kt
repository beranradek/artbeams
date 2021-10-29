package org.xbery.artbeams.common.antispam.repository

import org.xbery.artbeams.common.antispam.domain.AntispamQuiz
import org.xbery.artbeams.common.antispam.domain.AntispamQuizFilter
import org.xbery.overview.filter.Condition
import org.xbery.overview.mapper.Attr
import org.xbery.overview.mapper.Attribute
import org.xbery.overview.mapper.AttributeSource
import org.xbery.overview.mapper.DynamicEntityMapper
import org.xbery.overview.repo.Conditions

/**
 * Maps {@link AntispamQuiz} entity to set of attributes and vice versa.
 * @author Radek Beran
 */
open class AntispamQuizMapper() : DynamicEntityMapper<AntispamQuiz, AntispamQuizFilter>() {
    private val cls: Class<AntispamQuiz> = AntispamQuiz::class.java
    override fun getTableName() = "antispam_quiz"
    val questionAttr: Attribute<AntispamQuiz, String> =
        add(Attr.ofString(cls, "question").get { e -> e.question }.primary())
    val answerAttr: Attribute<AntispamQuiz, String> =
        add(Attr.ofString(cls, "answer").get { e -> e.answer })

    override fun createEntity(
        attributeSource: AttributeSource,
        attributes: List<Attribute<AntispamQuiz, *>>,
        aliasPrefix: String?
    ): AntispamQuiz {
        return AntispamQuiz(
            questionAttr.getValueFromSource(
                attributeSource,
                aliasPrefix
            ),
            answerAttr.getValueFromSource(attributeSource, aliasPrefix ?: "")
        )
    }

    override fun composeFilterConditions(filter: AntispamQuizFilter): MutableList<Condition> {
        val conditions = mutableListOf<Condition>()
        filter.question?.let { question -> conditions.add(Conditions.eq(this.questionAttr, question)) }
        return conditions
    }

    companion object {
        val Instance: AntispamQuizMapper = AntispamQuizMapper()
    }
}
