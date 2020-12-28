package munoon.bank.common.validation.money;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MoneyCountValidator implements ConstraintValidator<ValidMoneyCount, Double> {
    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String string = value.toString();
        int index = string.indexOf(".");
        return index == -1 || string.substring(index).length() <= 2;
    }
}
