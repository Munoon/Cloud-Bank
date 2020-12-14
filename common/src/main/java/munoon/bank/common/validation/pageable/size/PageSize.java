package munoon.bank.common.validation.pageable.size;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PageSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface PageSize {
    int min() default 0;

    int max();

    String message() default "Количество слишком большое или маленькое";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
