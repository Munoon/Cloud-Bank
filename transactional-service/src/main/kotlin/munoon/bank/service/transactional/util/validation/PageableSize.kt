package munoon.bank.service.transactional.util.validation

import org.springframework.data.domain.Pageable
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PageableSizeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class PageableSize(
        val min: Int = 0,
        val max: Int,
        val message: String = "Количество слишком большое или маленькое",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class PageableSizeValidator : ConstraintValidator<PageableSize, Pageable> {
    private var min: Int = 0
    private var max: Int = 0

    override fun isValid(pageable: Pageable, context: ConstraintValidatorContext?) =
            pageable.pageSize in (min + 1) until (max + 1)

    override fun initialize(pagebleSize: PageableSize) {
        min = pagebleSize.min
        max = pagebleSize.max
    }
}