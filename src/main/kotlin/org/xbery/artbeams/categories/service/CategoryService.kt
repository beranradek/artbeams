package org.xbery.artbeams.categories.service

import org.xbery.artbeams.categories.domain.Category
import org.xbery.artbeams.categories.domain.EditedCategory
import org.xbery.artbeams.common.context.OperationCtx

/**
 * @author Radek Beran
 */
interface CategoryService {
    fun findCategories(): List<Category>
    fun findCategoriesByArticleId(articleId: String): List<Category>
    fun saveCategory(edited: EditedCategory, ctx: OperationCtx): Category?
    fun findBySlug(slug: String): Category?
}
