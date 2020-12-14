package munoon.bank.service.transactional.controller

import munoon.bank.common.SecurityUtils
import munoon.bank.common.error.ErrorInfo
import munoon.bank.common.error.ErrorInfoField
import munoon.bank.common.error.ErrorType
import munoon.bank.common.error.RestExceptionHandler
import munoon.bank.common.util.exception.FieldValidationException
import munoon.bank.service.transactional.config.MongoConfig
import munoon.bank.service.transactional.util.NotEnoughBalanceException
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler : RestExceptionHandler() {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotEnoughBalanceException::class)
    fun notEnoughBalanceExceptionHandler(e: NotEnoughBalanceException, req: HttpServletRequest): ErrorInfo {
        log.warn("Not enough balance exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        return ErrorInfo(req.requestURL, ErrorType.NOT_ENOUGH_BALANCE, e.message)
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DuplicateKeyException::class)
    fun duplicateKeyExceptionHandler(e: DuplicateKeyException, req: HttpServletRequest): ErrorInfo {
        log.warn("Duplicate key exception on request '${req.requestURL}' for user ${SecurityUtils.authUserIdOrAnonymous()}", e)
        if (e.message != null) {
            val key = DATABASE_DUPLICATE_ERROR_EXCEPTION_MAP.keys.find { e.message!!.contains(it) }
            if (key != null) {
                val exception = DATABASE_DUPLICATE_ERROR_EXCEPTION_MAP.getValue(key)
                return ErrorInfoField(req.requestURL, mapOf(exception.field to listOf(exception.message)))
            }
        }
        return ErrorInfo(req.requestURL, ErrorType.VALIDATION_ERROR, e.message)
    }

    companion object {
        private val DATABASE_DUPLICATE_ERROR_EXCEPTION_MAP = mapOf(
                MongoConfig.UNIQUE_CARD_NUMBER_INDEX to FieldValidationException("number", "Такой номер карты уже существует!")
        )
    }
}