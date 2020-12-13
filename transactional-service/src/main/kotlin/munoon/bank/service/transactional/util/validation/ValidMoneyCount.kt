package munoon.bank.service.transactional.util.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MoneyCountValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class ValidMoneyCount(
        val message: String = "Сумма указана неверно",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class MoneyCountValidator : ConstraintValidator<ValidMoneyCount, Double> {
    override fun isValid(value: Double?, context: ConstraintValidatorContext): Boolean {
        val string = value?.toString() ?: return true
        val index = string.indexOf(".")
        return if (index == -1) true
               else string.substring(index).length <= 2
    }
}