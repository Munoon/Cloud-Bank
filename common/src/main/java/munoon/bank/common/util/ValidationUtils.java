package munoon.bank.common.util;

import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.*;

public class ValidationUtils {
    public static Map<String, List<String>> getErrorFieldMap(List<FieldError> fieldErrors) {
        Map<String, List<String>> result = new HashMap<>();
        fieldErrors.forEach(error -> {
            result.computeIfAbsent(error.getField(), k -> new ArrayList<>());
            result.get(error.getField()).add(error.getDefaultMessage());
        });
        return result;
    }

    public static Map<String, List<String>> getErrorFieldMap(Set<ConstraintViolation<?>> fieldErrors) {
        Map<String, List<String>> result = new HashMap<>();
        fieldErrors.forEach(error -> {
            String field = error.getPropertyPath().toString();
            result.computeIfAbsent(field, k -> new ArrayList<>());
            result.get(field).add(error.getMessage());
        });
        return result;
    }
}
