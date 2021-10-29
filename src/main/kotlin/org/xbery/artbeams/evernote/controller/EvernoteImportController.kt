package org.xbery.artbeams.evernote.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.evernote.service.EvernoteImporter
import javax.servlet.http.HttpServletRequest

/**
 * Evernote import routes.
 * @author Radek Beran
 */
@Controller
@RequestMapping("/admin/evernote")
open class EvernoteImportController(
    private val evernoteImporter: EvernoteImporter,
    private val common: ControllerComponents
) : BaseController(common) {

    @GetMapping("/import")
    fun evernoteImport(request: HttpServletRequest): Any {
        val model = try {
            val articles = evernoteImporter.importArticles()
            createModel(request, Pair("updatedArticles", articles))
        } catch (ex: Exception) {
            createModel(request, Pair("updatedArticles", listOf<Article>()), Pair("errorMessage", ex.toString()))
        }
        return ModelAndView("/evernote/import", model)
    }
}
