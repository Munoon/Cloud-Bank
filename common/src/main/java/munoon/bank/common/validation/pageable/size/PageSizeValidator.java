package munoon.bank.common.validation.pageable.size;

import org.springframework.data.domain.Pageable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PageSizeValidator implements ConstraintValidator<PageSize, Pageable> {
    private int min, max;

    @Override
    public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {
        return min <= pageable.getPageSize() && pageable.getPageSize() <= max;
    }

    @Override
    public void initialize(PageSize constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }
}
