package munoon.bank.common.validation.money;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MoneyCountValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidMoneyCount {
    String message() default "Сумма указана неверно";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
