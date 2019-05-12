package dev.cedrickring.auth.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@RestController
class CustomErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) ?: return "Unknown error"

        return when (status.toString().toInt()) {
            HttpStatus.FORBIDDEN.value() -> "Forbidden"
            HttpStatus.UNAUTHORIZED.value() -> "Unauthorized"
            HttpStatus.NOT_FOUND.value() -> "Page not found"
            HttpStatus.BAD_REQUEST.value() -> "Bad request"
            else -> "Unknown error"
        }
    }

    override fun getErrorPath(): String {
        return "/error"
    }

}
