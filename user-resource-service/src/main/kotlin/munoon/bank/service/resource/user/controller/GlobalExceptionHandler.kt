package munoon.bank.service.resource.user.controller

import munoon.bank.common.SecurityUtils
import munoon.bank.common.error.ErrorInfo
import munoon.bank.common.error.ErrorType
import munoon.bank.service.resource.user.util.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionHandler(e: NotFoundException, req: HttpServletRequest): ErrorInfo {
        log.warn("Not found exception on request ${req.requestURL} for user ${SecurityUtils.authUserId()}", e)
        return ErrorInfo(req.requestURL, ErrorType.NOT_FOUND, e.message)
    }
}