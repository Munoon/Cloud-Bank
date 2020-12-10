package munoon.bank.service.resource.user.controller

import munoon.bank.common.SecurityUtils
import munoon.bank.common.error.ErrorInfo
import munoon.bank.common.error.ErrorInfoField
import munoon.bank.common.error.ErrorType
import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.common.util.ValidationUtils
import munoon.bank.common.util.exception.FieldValidationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.lang.IllegalArgumentException
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException
import org.hibernate.exception.ConstraintViolationException as HibernateConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionHandler(e: NotFoundException, req: HttpServletRequest): ErrorInfo {
        log.warn("Not found exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfo(req.requestURL, ErrorType.NOT_FOUND, e.message)
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(FieldValidationException::class)
    fun fieldValidationExceptionHandler(e: FieldValidationException, req: HttpServletRequest): ErrorInfo {
        log.warn("Field validation exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfoField(req.requestURL, mapOf(e.field to listOf(e.message)))
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException::class)
    fun noHandlerFoundExceptionHandler(e: NoHandlerFoundException, req: HttpServletRequest): ErrorInfo {
        log.warn("No handler found exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfo(req.requestURL, ErrorType.NOT_FOUND, "Страница не найдена")
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException::class)
    fun accessDeniedExceptionHandler(e: AccessDeniedException, req: HttpServletRequest): ErrorInfo {
        log.warn("Access denied exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfo(req.requestURL, ErrorType.ACCESS_DENIED, e.message)
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun dataIntegrityViolationExceptionHandler(e: DataIntegrityViolationException, req: HttpServletRequest): ErrorInfo {
        log.warn("DataIntegrityViolationException on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return when (e.cause) {
            is HibernateConstraintViolationException -> {
                val constraintName = (e.cause as HibernateConstraintViolationException).constraintName
                ErrorInfo(req.requestURL, ErrorType.DATA_ERROR, DATABASE_ERROR_MAP[constraintName])
            }
            else -> ErrorInfo(req.requestURL, ErrorType.DATA_ERROR, e.message)
        }
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(IllegalArgumentException::class, MethodArgumentTypeMismatchException::class, HttpMessageNotReadableException::class)
    fun illegalRequestDataExceptionHandler(e: Exception, req: HttpServletRequest): ErrorInfo {
        log.warn("Illegal request data exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfo(req.requestURL, ErrorType.VALIDATION_ERROR, e.message)
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BindException::class)
    fun bindExceptionHandler(e: BindException, req: HttpServletRequest): ErrorInfo {
        log.warn("Bind exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        val validationErrorMap = ValidationUtils.getErrorFieldMap(e.bindingResult.fieldErrors)
        return ErrorInfoField(req.requestURL, validationErrorMap)
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(e: MethodArgumentNotValidException, req: HttpServletRequest): ErrorInfo {
        log.warn("Method argument not valid exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        val validationErrorMap = ValidationUtils.getErrorFieldMap(e.bindingResult.fieldErrors)
        return ErrorInfoField(req.requestURL, validationErrorMap)
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationExceptionHandler(e: ConstraintViolationException, req: HttpServletRequest): ErrorInfo {
        log.warn("Constraint violation exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        val validationErrorMap = ValidationUtils.getErrorFieldMap(e.constraintViolations)
        return ErrorInfoField(req.requestURL, validationErrorMap)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun exceptionHandler(e: Exception, req: HttpServletRequest): ErrorInfo {
        log.error("Unknown exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfo(req.requestURL, ErrorType.APPLICATION_EXCEPTION, e.message)
    }

    companion object {
        private val DATABASE_ERROR_MAP = mapOf(
                "users_username_key" to "Пользователь с таким логином уже существует",
                "users_role_user_id_role_key" to "Роль пользователя повторяется"
        )
    }
}