package munoon.bank.common.error;

import lombok.extern.slf4j.Slf4j;
import munoon.bank.common.util.ValidationUtils;
import munoon.bank.common.util.exception.FieldValidationException;
import munoon.bank.common.util.exception.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static munoon.bank.common.SecurityUtils.authUserIdOrAnonymous;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    private static final Map<String, String> DATABASE_ERROR_MAP = Map.of(
            "users_username_key", "Пользователь с таким логином уже существует",
            "users_role_user_id_role_key", "Роль пользователя повторяется"
    );

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo notFoundExceptionHandler(NotFoundException e, HttpServletRequest req) {
        log.warn("Not found exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        return new ErrorInfo(req.getRequestURL(), ErrorType.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(FieldValidationException.class)
    public ErrorInfoField fieldValidationExceptionHandler(FieldValidationException e, HttpServletRequest req) {
        log.warn("Field validation exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        return new ErrorInfoField(req.getRequestURL(), singletonMap(e.getField(), singletonList(e.getMessage())));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorInfo noHandlerFoundExceptionHandler(NoHandlerFoundException e, HttpServletRequest req) {
        log.warn("No handler found exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        return new ErrorInfo(req.getRequestURL(), ErrorType.NOT_FOUND, "Страница не найдена");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorInfo accessDeniedExceptionHandler(AccessDeniedException e, HttpServletRequest req) {
        log.warn("Access denied exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        return new ErrorInfo(req.getRequestURL(), ErrorType.ACCESS_DENIED, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e, HttpServletRequest req) {
        log.warn("Data integrity violation exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        if (e.getCause() instanceof ConstraintViolationException) {
            String constraintName = ((ConstraintViolationException) e.getCause()).getConstraintName();
            String message = Optional.ofNullable(DATABASE_ERROR_MAP.get(constraintName))
                    .orElse(e.getMessage());
            return new ErrorInfo(req.getRequestURL(), ErrorType.DATA_ERROR, message);
        }
        return new ErrorInfo(req.getRequestURL(), ErrorType.DATA_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataExceptionHandler(Exception e, HttpServletRequest req) {
        log.warn("Illegal request data exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        return new ErrorInfo(req.getRequestURL(), ErrorType.VALIDATION_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BindException.class)
    public ErrorInfo bindExceptionHandler(BindException e, HttpServletRequest req) {
        log.warn("Bind exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        Map<String, List<String>> errorFieldMap = ValidationUtils.getErrorFieldMap(e.getBindingResult().getFieldErrors());
        return new ErrorInfoField(req.getRequestURL(), errorFieldMap);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorInfo methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest req) {
        log.warn("Method argument not valid exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        Map<String, List<String>> errorFieldMap = ValidationUtils.getErrorFieldMap(e.getBindingResult().getFieldErrors());
        return new ErrorInfoField(req.getRequestURL(), errorFieldMap);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ErrorInfo constraintViolationExceptionHandler(javax.validation.ConstraintViolationException e, HttpServletRequest req) {
        log.warn("Constraint violation exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        Map<String, List<String>> errorFieldMap = ValidationUtils.getErrorFieldMap(e.getConstraintViolations());
        return new ErrorInfoField(req.getRequestURL(), errorFieldMap);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorInfo exceptionHandler(Exception e, HttpServletRequest req) {
        log.error("Unknown exception on request '{}' for user {}", req.getRequestURL(), authUserIdOrAnonymous(), e);
        return new ErrorInfo(req.getRequestURL(), ErrorType.APPLICATION_EXCEPTION, e.getMessage());
    }
}
