package munoon.bank.common.util.exception;

import lombok.Getter;

public class FieldValidationException extends RuntimeException {
    @Getter
    private final String field;

    public FieldValidationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public FieldValidationException(String message, Throwable cause, String field) {
        super(message, cause);
        this.field = field;
    }
}
