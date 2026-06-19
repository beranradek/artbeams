package org.xbery.artbeams.courses.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import jakarta.servlet.http.HttpServletRequest
import org.xbery.artbeams.common.controller.BaseController
import org.xbery.artbeams.common.controller.ControllerComponents
import org.xbery.artbeams.courses.service.CourseService

/**
 * Member-facing courses controller.
 */
@Controller
class CourseController(
    private val courseService: CourseService,
    common: ControllerComponents
) : BaseController(common) {

    @GetMapping("/clenska-sekce/courses")
    fun listCourses(request: HttpServletRequest): Any {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return redirect("/login")
        val courses = courseService.findCoursesForUser(loggedUser.common.id)
        model["courses"] = courses
        return ModelAndView("member/courses/list", model)
    }

    @GetMapping("/clenska-sekce/courses/{slug}")
    fun courseDetail(request: HttpServletRequest, @PathVariable slug: String): Any {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return redirect("/login")
        val course = courseService.findBySlug(slug) ?: return notFound(request)
        model["course"] = course
        return ModelAndView("member/courses/detail", model)
    }

    @GetMapping("/clenska-sekce/courses/{slug}/modules/{moduleSlug}")
    fun moduleView(request: HttpServletRequest, @PathVariable slug: String, @PathVariable moduleSlug: String): Any {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return redirect("/login")
        val course = courseService.findBySlug(slug) ?: return notFound(request)
        val module = course.modules.find { it.id == moduleSlug }
            ?: return notFound(request)
        // For module articles we delegate to per-course search but filter by module id
        val allArticles = courseService.searchArticlesInCourse(course.common.id, "", 100)
        val articles = allArticles.filter { it.moduleId == module.id }
        model["course"] = course
        model["module"] = module
        model["articles"] = articles
        return ModelAndView("member/courses/module", model)
    }

    /**
     * Per-course search — uses CourseService.searchArticlesInCourse to ensure only course-bound
     * (non-public) articles are returned.
     */
    @RequestMapping(value = ["/clenska-sekce/courses/{slug}/search"], method = [RequestMethod.GET, RequestMethod.POST])
    fun searchInCourse(request: HttpServletRequest, @PathVariable slug: String): Any {
        val model = createModel(request)
        val loggedUser = model["_loggedUser"] as? org.xbery.artbeams.users.domain.User
            ?: return redirect("/login")
        val course = courseService.findBySlug(slug) ?: return notFound(request)
        val q = request.getParameter("q") ?: ""
        val limit = try {
            request.getParameter("limit")?.toInt() ?: 50
        } catch (e: NumberFormatException) {
            50
        }
        val articles = courseService.searchArticlesInCourse(course.common.id, q, limit)
        model["course"] = course
        model["q"] = q
        model["articles"] = articles
        return ModelAndView("member/courses/detail", model)
    }
}
