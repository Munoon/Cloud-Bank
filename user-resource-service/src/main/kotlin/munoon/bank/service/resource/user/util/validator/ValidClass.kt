package munoon.bank.service.resource.user.util.validator

import munoon.bank.service.resource.user.config.ClassesProperties
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidClassValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class ValidClass(
        val message: String = "Такого класса не существует",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class ValidClassValidator(private val classesProperties: ClassesProperties) : ConstraintValidator<ValidClass, String> {
    override fun isValid(value: String, context: ConstraintValidatorContext?) =
            classesProperties.classes.contains(value)
}