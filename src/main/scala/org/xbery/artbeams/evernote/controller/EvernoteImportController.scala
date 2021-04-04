package org.xbery.artbeams.evernote.controller

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping}
import org.springframework.web.servlet.ModelAndView
import org.xbery.artbeams.articles.domain.Article
import org.xbery.artbeams.common.controller.{BaseController, ControllerComponents}
import org.xbery.artbeams.evernote.service.EvernoteImporter

/**
  * Evernote import routes.
  * @author Radek Beran
  */
@Controller
@RequestMapping(Array("/admin/evernote"))
class EvernoteImportController @Inject() (evernoteImporter: EvernoteImporter, common: ControllerComponents) extends BaseController(common) {

  @GetMapping(Array("/import"))
  def evernoteImport(request: HttpServletRequest): Any = {
    val articlesOrError = evernoteImporter.importArticles()
    val model = articlesOrError match {
      case Left(ex) =>
        createModel(request, "updatedArticles" -> Seq.empty[Article], "errorMessage" -> ex.toString)
      case Right(articles) =>
        createModel(request, "updatedArticles" -> articles)
    }
    new ModelAndView("/evernote/import", model)
  }
}
